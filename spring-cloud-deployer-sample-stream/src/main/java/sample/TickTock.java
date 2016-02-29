/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.deployer.resource.maven.MavenResource;
import org.springframework.cloud.deployer.spi.local.LocalProcessDeployer;
import org.springframework.cloud.deployer.spi.process.ProcessDefinition;
import org.springframework.cloud.deployer.spi.process.ProcessDeploymentId;
import org.springframework.cloud.deployer.spi.process.ProcessDeploymentRequest;

/**
 * @author Mark Fisher
 */
public class TickTock {

	public static void main(String[] args) throws InterruptedException {
		LocalProcessDeployer deployer = new LocalProcessDeployer();
		ProcessDeploymentId logId = deployer.deploy(createProcessDeploymentRequest("log-sink", "ticktock"));
		ProcessDeploymentId timeId = deployer.deploy(createProcessDeploymentRequest("time-source", "ticktock"));
		for (int i = 0; i < 12; i++) {
			Thread.sleep(5 * 1000);
			System.out.println("time: " + deployer.status(timeId));
			System.out.println("log:  " + deployer.status(logId));
		}
		deployer.undeploy(timeId);
		deployer.undeploy(logId);
		System.out.println("time after undeploy: " + deployer.status(timeId));
		System.out.println("log after undeploy:  " + deployer.status(logId));
	}

	private static ProcessDeploymentRequest createProcessDeploymentRequest(String app, String stream) {
		MavenResource resource = new MavenResource.Builder()
				.setArtifactId(app)
				.setGroupId("org.springframework.cloud.stream.module")
				.setVersion("1.0.0.BUILD-SNAPSHOT")
				.setExtension("jar")
				.setClassifier("exec")
				.build();
		Map<String, String> properties = new HashMap<>();
		properties.put("server.port", "0");
		if (app.endsWith("-source")) {
			properties.put("spring.cloud.stream.bindings.output.destination", stream);
		}
		else {
			properties.put("spring.cloud.stream.bindings.input.destination", stream);
			properties.put("spring.cloud.stream.bindings.input.group", "default");
		}
		ProcessDefinition definition = new ProcessDefinition(app, stream, properties);
		ProcessDeploymentRequest request = new ProcessDeploymentRequest(definition, resource);
		return request;
	}
}
