package com.essi.dependency.functionalities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.essi.dependency.components.Clause;
import com.essi.dependency.components.Dependency;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONHandler {

	/**
	 * Constructor
	 */
	public JSONHandler() {
		super();

	}

	/**
	 * Read requirements from the selected project of the input JSON
	 * 
	 * @param jsonData
	 * @param projectId
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public List<List<String>> readRequirement(String jsonData, String projectId)
			throws JsonProcessingException, IOException {
		List<List<String>> requirms = new ArrayList<>();

		// create ObjectMapper instance
		ObjectMapper objectMapper = new ObjectMapper();

		// Get ids of the project
		JsonNode rootNode = objectMapper.readTree(jsonData);
		JsonNode projectNode = rootNode.get("projects");
		ArrayList<String> reqIds = new ArrayList<>();
		for (JsonNode child : projectNode) {
			String id = child.get("id").asText();
			if (id.equals(projectId)) {
				for (JsonNode r : child.get("specifiedRequirements")) {
					reqIds.add(r.asText());
				}
			}
		}
		
		// get requirements
		JsonNode reqNode = rootNode.get("requirements");
		for (JsonNode child : reqNode) {
			String id = child.get("id").asText();
			if (reqIds.contains(id)) {
				List<String> newReq = new ArrayList<>();
				newReq.add(id);
				newReq.add(child.get("name").asText());
				newReq.add(child.get("text").asText());
				if (child.has("comments")) {
					for (final JsonNode comment : child.get("comments")) {
						newReq.add(comment.get("text").asText());
					}
				}
				requirms.add(newReq);
			}
		}
		return requirms;

	}

	/**
	 * Read all the dependencies from the JSON and return an array with all of them.
	 * 
	 * @param jsonData
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public List<List<Object>> readDependency(String jsonData) throws JsonProcessingException, IOException {
		List<List<Object>> deps = new ArrayList<>();

		// create ObjectMapper instance
		ObjectMapper objectMapper = new ObjectMapper();
		// Get values
		JsonNode rootNode = objectMapper.readTree(jsonData);
		if (rootNode.has("dependencies")) {
			JsonNode depNode = rootNode.get("dependencies");
			for (JsonNode child : depNode) {
				String dependencyType = child.get("dependency_type").asText();
				String status = child.get("status").asText();
				String from = child.get("fromid").asText();
				String to = child.get("toid").asText();
				JsonNode description = child.get("description");

				List<Object> dependency = new ArrayList<>();
				dependency.add(dependencyType);
				dependency.add(status);
				dependency.add(from);
				dependency.add(to);
				dependency.add(description);

				deps.add(dependency);
			}
		}
		return deps;

	}

	/**
	 * Store detected dependencies if they are not previously contained in the JSON.
	 * 
	 * @param jsonData
	 * @param newDeps
	 * @return
	 * @throws IOException
	 */
	public ObjectNode storeDependencies(String jsonData, List<Object> newDeps) throws IOException {
		List<List<Object>> deps = new ArrayList<>();
		ArrayList<ObjectNode> oldDeps = new ArrayList<>();
		// create ObjectMapper instance
		ObjectMapper objectMapper = new ObjectMapper();

		// Read previous dependencies if any
		ObjectNode objectNode = objectMapper.createObjectNode();
		if (!jsonData.equals("")) {
			JsonNode rootNode = objectMapper.readTree(jsonData);
			objectNode = (ObjectNode) rootNode;

			deps = readDependency(jsonData);
		}

		// parse the old dependencies
		ArrayNode depArrayNode = objectMapper.createArrayNode();

		for (List<Object> node : deps) {
			ObjectNode objN = objectMapper.createObjectNode();
			objN.put("dependency_type", ((String) node.get(0)).toLowerCase());
			objN.put("status", ((String) node.get(1)).toLowerCase());
			objN.put("fromid", (String) node.get(2));
			objN.put("toid", (String) node.get(3));
			objN.set("description", (JsonNode) node.get(4));
			oldDeps.add(objN);
			depArrayNode.add(objN);
		}

		// Create new dependencies
		for (Object d : newDeps) {
			ObjectNode objN = objectMapper.createObjectNode();
			objN.put("dependency_type", ((Dependency) d).getDependencyType().toString().toLowerCase());
			objN.put("status", ((Dependency) d).getStatus().toString().toLowerCase());
			objN.put("fromid", ((Dependency) d).getFrom());
			objN.put("toid", ((Dependency) d).getTo());
			ArrayNode description = objectMapper.createArrayNode();
			ObjectNode node = objectMapper.createObjectNode();
			node.put("component", "cross-reference detection");
			description.add(node);
			objN.set("description", description);

			// Store new dependencies if they are not already contained in the JSON
			if (!isContained(oldDeps, objN)) {
				depArrayNode.add(objN);
			}
		}
		// Update the JSON with the dependencies (old and new)
		objectNode.set("dependencies", depArrayNode);
		return objectNode;
	}

	/**
	 * Create the requirements from the extracted file
	 * 
	 * @param objectNode
	 * @param clauseList
	 * @return
	 */
	public ObjectNode storeRequirements(ObjectNode objectNode, List<Object> clauseList) {
		
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayNode reqArrayNode = objectMapper.createArrayNode();
		for (Object clause : clauseList) {
			ObjectNode objN = objectMapper.createObjectNode();
			objN.put("id", Integer.toString(((Clause) clause).getId()));
			objN.put("name", Integer.toString(((Clause) clause).getId()));
			objN.put("text", ((Clause) clause).getClauseString());
			objN.put("sect", ((Clause) clause).getSect());
			objN.put("subsect", ((Clause) clause).getSubsect());
			objN.put("parag", ((Clause) clause).getParag());
			objN.put("subparag", ((Clause) clause).getSubparg());
			reqArrayNode.add(objN);
		}
		objectNode.set("requirements", reqArrayNode);
		return objectNode;
	}

	/**
	 * Create new project from the input file to store in a new JSON.
	 * 
	 * @param objectNode
	 * @param clauseList
	 * @return
	 */
	public ObjectNode createProject(ObjectNode objectNode, List<Object> clauseList) {

		ObjectMapper objectMapper = new ObjectMapper();
		ArrayNode ids = objectMapper.createArrayNode();

		ArrayNode arrayNode = objectMapper.createArrayNode();
		for (Object clause : clauseList) {
			ids.add(Integer.toString(((Clause) clause).getId()));
		}
		ObjectNode objN = objectMapper.createObjectNode();
		objN.put("id", "file");
		objN.put("name", ((Clause) clauseList.get(0)).getDoc());
		objN.set("specifiedRequirements", ids);
		arrayNode.add(objN);

		objectNode.set("projects", arrayNode);
		return objectNode;
	}

	/**
	 * Check if a dependency is contained in a list of dependencies.
	 * @param oldDeps
	 * @param objN
	 * @return
	 */
	private boolean isContained(ArrayList<ObjectNode> oldDeps, ObjectNode objN) {

		for (ObjectNode node : oldDeps) {
			if (node.get("toid").asText().equals(objN.get("toid").asText())
					&& node.get("fromid").asText().equals(objN.get("fromid").asText())
					&& node.get("dependency_type").asText().equals(objN.get("dependency_type").asText())) {
				return true;
			}
		}
		return false;
	}

}
