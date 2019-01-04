/*
 * Copyright © 2014-2017 Capco. All rights reserved.
 * Capco Digital Framework.
 */
package com.prot.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.gradle.api.plugins.JavaPlugin.*;

public class WholeTestingPlugin implements Plugin<Project> {
    private String[] supportLangs = new String[] {"java"};		// java groovy scala kotlin

	@Override
	public void apply(Project project) {
		final JavaPluginConvention javaPlugin = project.getConvention().getPlugin(JavaPluginConvention.class);
		final SourceSetContainer sourceSets = javaPlugin.getSourceSets();
		final SourceSet getMain = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
		final SourceSet main = getMain == null ? sourceSets.create(SourceSet.MAIN_SOURCE_SET_NAME) : getMain;
		final SourceSet integrationTest = sourceSets.create("integrationTest");
		if (integrationTest != null) {
            configWholeTestSourceSet(project, main, integrationTest, "integTest");

            project.getTasks().create("integrationTest", IntegrationTestTask.class, (task) -> {
                task.setDescription("Run integration tests");
                task.setGroup("verification");
                task.setTestClassesDir(integrationTest.getOutput().getClassesDir());
                task.setClasspath(integrationTest.getRuntimeClasspath());

                Map<String, Object> sysProps = new HashMap<>(64);
                for (Object k : System.getProperties().keySet()) {
                    sysProps.put((String) k, System.getProperty((String) k));
                }
                task.systemProperties(sysProps);
                task.mustRunAfter("test");
            });
        }
	}

	private void configWholeTestSourceSet(Project project, SourceSet main, SourceSet wholeTest, String folderName) {
        Arrays.stream(supportLangs).forEach(lang -> {
            wholeTest.getJava().srcDir("src/" + folderName + "/" + lang);
        });
        wholeTest.getResources().srcDir("src/" + folderName + "/resources");
        wholeTest.setCompileClasspath( project.files(wholeTest.getCompileClasspath(), main.getOutput(), project.getConfigurations().getByName(TEST_RUNTIME_CONFIGURATION_NAME)) );
        wholeTest.setRuntimeClasspath( project.files(wholeTest.getRuntimeClasspath(), wholeTest.getOutput(), wholeTest.getCompileClasspath()) );
	}
}
