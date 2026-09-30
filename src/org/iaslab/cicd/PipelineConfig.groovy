package org.iaslab.cicd

class PipelineConfig implements Serializable {
    String serviceName
    String buildType = 'maven'
    String jdkVersion = '17'
    String publishJar = true
    String publishDocker = true
    String nexusHost
    String dockerport = '9080'
    String deployTarget
    String healthEndpoint = '/api/products'

    PipelineConfig(Map config) {
        if(!config.serviceName) {
            throw new IllegalArgumentException("serviceName is required")
        }
        if(!config.nexusHost) {
            throw new IllegalArgumentException("nexusHost is required")
        }
        if(!config.deployTarget) {
            throw new IllegalArgumentException("deployTarget is required")
        }

        this.serviceName = config.serviceName
        this.buildType = config.buildType ?: this.buildType
        this.jdkVersion = config.jdkVersion ?: this.jdkVersion
        this.publishJar = config.publishJar != null ? config.publishJar : this.publishJar
        this.publishDocker = config.publishDocker != null ? config.publishDocker : this.publishDocker
        this.nexusHost = config.nexusHost
        this.dockerport = config.dockerport ?: this.dockerport
        this.deployTarget = config.deployTarget
        this.healthEndpoint = config.healthEndpoint ?: this.healthEndpoint
    }
}