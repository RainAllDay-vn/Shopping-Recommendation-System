from langchain.agents import initialize_agent, Tool
from langchain.agents.agent_types import AgentType
from langchain_google_vertexai import ChatVertexAI
from langchain.schema import Document
from langchain_qdrant import QdrantVectorStore
from typing import List
import re

"""
Thing need to be done: different tools for multiple purposes
- (check) filter by metadata
- (check) semantic_search_on_ids
- (uncheck) filter promotion packages
- (uncheck) improve reasoning for agent
"""

def parse_price(price_str: str) -> float:
    """Utility to convert price string like '14990000' to float."""
    price_clean = re.sub(r"[^\d.]", "", price_str)
    return float(price_clean) if price_clean else float("inf")

def filter_by_metadata(vector_store: QdrantVectorStore, max_price: float = None) -> List[Document]:
    """Filter documents by price (in VND or similar)."""
    all_docs = vector_store.similarity_search(".*", k=1000)  # retrieve everything (or a big chunk)
    filtered = []

    for doc in all_docs:
        metadata = doc.metadata or {}
        price_str = metadata.get("data-price", "")
        price_val = parse_price(price_str)

        if max_price is None or price_val <= max_price:
            filtered.append(doc)

    return filtered

def semantic_search_on_ids(ids: List[str], query: str, vector_store: QdrantVectorStore, k: int = 3) -> List[Document]:
    """Perform semantic search only within documents that match the given data-ids."""
    all_docs = vector_store.similarity_search(query, k=100)  # optional: increase k
    filtered = [doc for doc in all_docs if doc.metadata.get("data-id") in ids]

    return filtered[:k]

tools = [
    Tool(
        name="filter_by_metadata",
        func=filter_by_metadata,
        description="Use to filter products by brand or price"
    ),
    Tool(
        name="semantic_search",
        func=semantic_search_on_ids,
        description="Use to search products semantically"
    ),
]

llm = ChatVertexAI(model="gemini-pro")  

agent = initialize_agent(
    tools=tools,
    llm=llm,
    agent=AgentType.ZERO_SHOT_REACT_DESCRIPTION,
    verbose=True,
)

#agent.run("")
