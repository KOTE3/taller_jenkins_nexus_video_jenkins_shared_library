def call(Map params = [:]) {
    String serviceName = params.serviceName
    String nexusHost = params.nexusHost
    String dockerport = params.dockerport ?: '9080'
    boolean publishJar = params.publishJar != null ? params.publishJar : true
    boolean publishDocker = params.publishDocker != null ? params.publishDocker : true

    String commitHash = env.GIT_COMMIT ? env.GIT_COMMIT.take(8) : 'unknown'
    String imageTag = "${env.BUILD_NUMBER}-${commitHash}"
    String registryUrl = "${nexusHost}:${dockerport}"
    String fullImageName = "${registryUrl}/${serviceName}:${imageTag}"

    if (publishJar) {
        echo "Publishing JAR to Nexus..."
        withCredentials([usernamePassword(credentialsId: 'nexus-credentials', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
            sh """
                mvn -B clean deploy -DskipTests \\
                    -DaltDeploymentRepository=nexus::default::http://\$NEXUS_USER:\$NEXUS_PASS@${nexusHost}:8081/repository/maven-snapshots/
            """
        }
    }

    if(publishDocker) {
        echo "Construyendo imagen Docker: ${fullImageName}..."
        sh "docker build -t ${fullImageName} ."

        echo ">> Publicando en Nexus Docker Registry..."
        withCredentials([usernamePassword(credentialsId: 'nexus-credentials', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
            sh """
                echo "\$NEXUS_PASS" | docker login ${registryUrl} -u "\$NEXUS_USER" --password-stdin
                docker push ${fullImageName}
                docker logout ${registryUrl}
            """
        }
    }

    return [imageTag: imageTag, fullImageName: fullImageName]
}