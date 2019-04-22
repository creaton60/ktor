import org.jetbrains.kotlin.gradle.plugin.mpp.*

description = "Ktor network utilities"

val ideaActive: Boolean by project.extra

kotlin {
    val current = mutableListOf<String>()
    if (ideaActive) {
        current.add("posix")
    } else {
        current.addAll(listOf("macosX64", "linuxX64", "mingwX64", "iosArm64", "iosArm32", "iosX64"))
    }

    current.map { targets.getByName(it) }.filterIsInstance<KotlinNativeTarget>().forEach {
        it.compilations.getByName("main") {
            val select by cinterops.creating {
                defFile = File(projectDir, "posix/interop/select.def")
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":ktor-utils"))
            }
        }
    }
}
