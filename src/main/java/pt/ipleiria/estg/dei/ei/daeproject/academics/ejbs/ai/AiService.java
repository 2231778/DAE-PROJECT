package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs.ai;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// This makes the bean available for injection throughout your application
@ApplicationScoped
public class AiService {

    // The endpoint of your local AI container
    private static final String OLLAMA_API_URL = "http://host.docker.internal:11434/api/generate";

    // You could make the model name configurable
    private static final String AI_MODEL = "llama3"; // Or "mistral", "gemma", etc.

    /**
     * Generates a summary for a given text.
     *
     * @param textToSummarize The full text of the publication.
     * @return The AI-generated summary as a String.
     */
    public String generateSummary(String textToSummarize) {
        // 1. Create a JAX-RS client
        Client client = ClientBuilder.newClient();

        // 2. Define the prompt for the AI
        String prompt = "Say in a summary what the following text suggests( 1 paragraph ): \n\n" + textToSummarize;

        // 3. Create the request payload (body) for the Ollama API
        // This needs to be a JSON object matching the API's expected format.
        String requestBody = "{"
                + "\"model\": \"" + AI_MODEL + "\","
                + "\"prompt\": \"" + escapeJson(prompt) + "\","
                + "\"stream\": false" // We want the full response at once
                + "}";

        try {
            // 4. Make the POST request
            Response response = client.target(OLLAMA_API_URL)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(requestBody));

            // 5. Check for success and process the response
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String jsonResponse = response.readEntity(String.class);

                // The actual summary is inside a "response" field in the JSON.
                // You should use a proper JSON parsing library (like Jakarta JSON Binding or Gson)
                // for robustness, but for a quick example, we can parse it manually.
                return parseResponse(jsonResponse);
            } else {
                // Handle errors
                System.err.println("AI Service request failed: " + response.getStatus());
                System.err.println("Response: " + response.readEntity(String.class));
                return "Error: Could not generate summary.";
            }

        } finally {
            // 6. Always close the client
            client.close();
        }
    }

    // Helper to escape quotes in the text for valid JSON
    private String escapeJson(String text) {
        return text.replace("\"", "\\\"").replace("\n", "\\n");
    }

    // Helper to extract the summary from the JSON response
    private String parseResponse(String jsonResponse) {
        // A very basic parser. A real JSON library is much better.
        // Example response: {"model":"llama3", "created_at":"...", "response":"This is the summary...", ...}
        if (jsonResponse.contains("\"response\":\"")) {
            int start = jsonResponse.indexOf("\"response\":\"") + 12;
            int end = jsonResponse.indexOf("\"", start);
            if (end > start) {
                return jsonResponse.substring(start, end);
            }
        }
        return "Summary could not be extracted.";
    }
}
