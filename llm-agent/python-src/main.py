from langchain_google_vertexai import VertexAIEmbeddings
from langchain_qdrant import QdrantVectorStore
from google.cloud import aiplatform
from langchain_google_vertexai import ChatVertexAI
"""
Thing need to be done:
- (check) load existing vector data  function
- (check) asking llm func
- (uncheck) improve prompt
- (uncheck) conversational chat
"""
aiplatform.init(project="teak-kit-453818-e0", location="us-central1")
"""
User need to set up environment variable GOOGLE_APPLICATION_CREDENTIALS to the path of the service account key file .json
"""

def load_existing_vector_store(collection_name: str,    
                               persist_path: str) -> QdrantVectorStore:
    embeddings = VertexAIEmbeddings(model="textembedding-gecko-multilingual@001")
    
    vector_store = QdrantVectorStore.from_existing_collection(collection_name=collection_name,
                                                                path=persist_path,
                                                                embedding=embeddings)
    
    return vector_store

def ask_llm_with_context(query: str, vector_store, k: int = 3) -> str:
    results = vector_store.similarity_search(query, k=k)

    context_chunks = []
    for i, doc in enumerate(results, 1):
        metadata = doc.metadata or {}
        name = metadata.get("data-name", "Unknown name")
        price = metadata.get("data-price", "Unknown price")
        snippet = doc.page_content.strip()

        formatted = f"{i}. {name} - {price} VND\n{snippet}"
        context_chunks.append(formatted)

    context = "\n\n".join(context_chunks)

    prompt = f"""
    You are a helpful laptop shopping assistant.
    A user asked: "{query}"

    Here are some related product descriptions and prices:

    {context}

    Based on the above, respond to the user's request in a clear and helpful way.
    """
    llm = ChatVertexAI(model="gemini-2.0-flash-001",
                        temperature=0,
                        max_tokens=None,
                        max_retries=6,
                        stop=None)


    ai_msg = llm.invoke(prompt)

    return ai_msg.content.strip()

""""
def search_similar_docs(vector_store: QdrantVectorStore, 
                        query: str, 
                        k: int = 3):
    results = vector_store.similarity_search(query, k=k)
    print(f"\nTop {k} results for: '{query}'\n")
    for i, doc in enumerate(results, 1):
        print(f"{i}. {doc.page_content[:150]}...")
        print(f"   Metadata: {doc.metadata}\n")
"""

if __name__ == "__main__":
    collection_name = "TGDD_collection"  
    persist_path = r"F:\GitHub\RAG-with-metadata-filtering\vector-store\TGDD_collection"

    vector_store = load_existing_vector_store(collection_name, persist_path)

    while True:
        user_query = input("\nAsk your question (or type 'exit'): ")
        if user_query.lower() == "exit":
            break

        answer = ask_llm_with_context(user_query, vector_store, k=3)
        print("\n Response:\n")
        print(answer)
