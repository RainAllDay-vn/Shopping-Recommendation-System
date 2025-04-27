from langchain_google_vertexai import VertexAIEmbeddings
from langchain_qdrant import QdrantVectorStore
from google.cloud import aiplatform
from langchain_google_vertexai import ChatVertexAI
from langchain.chains import ConversationalRetrievalChain
from langchain.memory import ConversationBufferMemory

def load_existing_vector_store(collection_name: str,
                               persist_path: str) -> QdrantVectorStore:
    embeddings = VertexAIEmbeddings(model="textembedding-gecko-multilingual@001")

    vector_store = QdrantVectorStore.from_existing_collection(collection_name=collection_name,
                                                              path=persist_path,
                                                              embedding=embeddings)

    return vector_store

def setup_conversational_chain(vector_store):
    llm = ChatVertexAI(model="gemini-2.0-flash-001", temperature=0)

    memory = ConversationBufferMemory(
        memory_key="chat_history",
        return_messages=True
    )

    retriever = vector_store.as_retriever(search_kwargs={"k": 10})

    chain = ConversationalRetrievalChain.from_llm(
        llm=llm,
        retriever=retriever,
        memory=memory,
        verbose=False,
    )
    return chain
if __name__ == "__main__":
    collection_name = "TGDD_collection"
    persist_path = r"F:\GitHub\RAG-with-metadata-filtering\vector-store\TGDD_collection"

    vector_store = load_existing_vector_store(collection_name, persist_path)
    chain = setup_conversational_chain(vector_store)

    print("Laptop Shopping Assistant - Ask me anything!\n")

    while True:
        user_query = input("You: ")
        if user_query.lower() == "exit":
            break

        response = chain.run(user_query)
        print(f"\nAI: {response}\n")
