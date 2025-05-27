# ✨ ShopX: Intelligent Shopping Recommendation System


## Quick‑start
### 0  Clone this repository
```bash
git clone https://github.com/RainAllDay-vn/Shopping-Recommendation-System.git
```

### 1  Run Qdrant locally with Docker
```bash
# Pull the latest image
docker pull qdrant/qdrant

# Start Qdrant – REST 6333, gRPC 6334, data persisted in ./qdrant_storage

docker run -p 6333:6333 -p 6334:6334 \
  -v "$(pwd)/qdrant_storage:/qdrant/storage:z" \
  qdrant/qdrant
```
*Open <http://localhost:6333/dashboard> to inspect collections.*

---

### 2  Export environment variables
| Variable                        | Purpose |
|---------------------------------|---------|
| `VERTEX_AI_GEMINI_PROJECT_ID`   | **GCP project ID** that hosts Vertex AI |
| `VERTEX_AI_GEMINI_LOCATION`     | Region where Gemini / embeddings are available (`us‑central1`, `europe‑west4`, …) |
| `GOOGLE_APPLICATION_CREDENTIALS` | Absolute path to your **service‑account key** JSON file (must have *Vertex AI User* + *Storage Object Viewer* roles) |
| `VERTEX_AI_GEMINI_STORE_NAME`    | Name of your store |

```bash
# PowerShell
setx VERTEX_AI_GEMINI_PROJECT_ID "my‑gcp‑project"
setx VERTEX_AI_GEMINI_LOCATION "us‑central1"
setx GOOGLE_APPLICATION_CREDENTIALS "C:\\keys\\vertex‑sa.json"
setx VERTEX_AI_GEMINI_STORE_NAME "Shopping Recommendation System"
```
> **IDE users:** put the same key/value pairs in the Run‑Configuration → *Environment variables* box.

---

### 3  Build & run (Java src)
```bash
./mvnw clean package
```

### 4  Run the application
To launch the Shopping Recommendation System:

1. Navigate to the main entry point:
   src/main/java/com/project/shoppingrecommendationsystem/ApplicationLauncher.java
2. Run the ApplicationLauncher class using your IDE (e.g. IntelliJ, Eclipse) or the command line.

Once started, the application will load and you're ready to enjoy an intelligent shopping experience powered by AI!
## Cleaning up
```bash
# stop container
docker ps            # copy CONTAINER ID
docker stop <id>
# remove persisted vectors if you wish
rm -rf ./qdrant_storage
```

---

