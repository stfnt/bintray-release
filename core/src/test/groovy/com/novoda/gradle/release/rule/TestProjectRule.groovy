package com.novoda.gradle.release.rule

import org.junit.rules.TemporaryFolder
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestProjectRule implements TestRule {

    enum Project {
        JAVA, ANDROID
    }

    private def tempFolder = new TemporaryFolder()

    private final Project project

    TestProjectRule(Project project) {
        this.project = project
    }

    @Override
    Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            void evaluate() throws Throwable {
                tempFolder.create()
                createSourceCode()
                createAndroidManifest()
                createBuildScript()
                try {
                    base.evaluate()
                } finally {
                    tempFolder.delete()
                }
            }
        }
    }

    File getProjectDir() {
        tempFolder.root
    }

    private String createSourceCode() {
        new File(tempFolder.root, "src/main/java/HelloWorld.java").with {
            getParentFile().mkdirs()
            text = "public class HelloWorld {}"
        }
    }

    private void createAndroidManifest() {
        if (project == Project.ANDROID) {
            new File(tempFolder.root, "/src/main/AndroidManifest.xml").with {
                getParentFile().mkdirs()
                text = "<manifest package=\"com.novoda.test\"/>"
            }
        }
    }

    private void createBuildScript() {
        File gradleScript = new File(tempFolder.root, "build.gradle")
        switch (project) {
            case Project.JAVA:
                gradleScript.text = GradleScriptTemplates.java()
                break
            case Project.ANDROID:
                gradleScript.text = GradleScriptTemplates.android()
                break
            default:
                throw new IllegalArgumentException("$project should be a valid value!")
        }
    }


}
