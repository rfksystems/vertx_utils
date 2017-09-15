package com.rfksystems.vertx_utils


import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Verticle
import io.vertx.core.json.JsonObject
import io.vertx.core.spi.VerticleFactory

class ManagedVerticleFactory : VerticleFactory {
    companion object {
        val DEFAULT_PREFIX = "local"
    }

    private val configuration: JsonObject
    private val namePrefix: String

    constructor(configuration: JsonObject, namePrefix: String) {
        this.configuration = configuration
        this.namePrefix = namePrefix
    }

    constructor(configuration: JsonObject) {
        this.configuration = configuration
        this.namePrefix = DEFAULT_PREFIX
    }

    override fun requiresResolve(): Boolean = true

    override fun resolve(
            identifier: String,
            deploymentOptions: DeploymentOptions,
            classLoader: ClassLoader,
            resolution: Future<String>
    ) {
        try {
            val localIdentifier = VerticleFactory.removePrefix(identifier)

            if (!configuration.containsKey(localIdentifier)) {
                throw IllegalArgumentException("Undefined service: $localIdentifier")
            }

            val serviceConfiguration = configuration.getJsonObject(localIdentifier)
            val main = serviceConfiguration.getString("main")

            val depOptions = deploymentOptions.toJson()
            val depConfig = depOptions.getJsonObject("config", JsonObject())
            val serviceOptions = serviceConfiguration.getJsonObject("options", JsonObject())
            val serviceConfig = serviceOptions.getJsonObject("config", JsonObject())

            depOptions.mergeIn(serviceOptions)
            serviceConfig.mergeIn(depConfig)

            depOptions.put("config", serviceConfig)
            deploymentOptions.fromJson(depOptions)

            resolution.complete(main)
        } catch (e: Exception) {
            resolution.fail(e)
        }
    }

    override fun prefix(): String {
        return namePrefix
    }

    @Throws(Exception::class)
    override fun createVerticle(verticleName: String, classLoader: ClassLoader): Verticle {
        throw IllegalStateException("Shouldn't be called")
    }
}
