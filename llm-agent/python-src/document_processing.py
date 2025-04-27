import csv
from langchain.schema import Document
from langchain.text_splitter import RecursiveCharacterTextSplitter

"""
Thing need to be done: design a function to deal with data from other sources
- (check) TGDD_load_laptops_and_descriptions 
- (uncheck) FPT_load_laptops_and_descriptions 
- (uncheck) CellphoneS_load_laptops_and_descriptions 
- (uncheck) Decide which metadata to keep from source
- (uncheck) Any better text splitter?
"""
def TGDD_load_laptops_and_descriptions(laptop_csv: str, description_csv: str):
    metadata_map = {}
    
    with open(laptop_csv, mode='r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            data_id = row.get("data-id", "").strip()
            if not data_id:
                continue
            metadata_map[data_id] = {
                "data-id": data_id,
                "data-name": row.get("data-name", "").strip(),
                "data-price": row.get("data-price", "").strip(),
            }
    documents = []

    with open(description_csv, mode='r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            data_id = row.get("data-id", "").strip()
            description = row.get("description", "").strip()

            if not data_id or not description:
                continue  

            metadata = metadata_map.get(data_id)
            if metadata:
                doc = Document(
                    page_content=description,
                    metadata=metadata
                )
                documents.append(doc)
        splitter = RecursiveCharacterTextSplitter(chunk_size=700, chunk_overlap=50)
        split_docs = splitter.split_documents(documents)
    return split_docs

docs = TGDD_load_laptops_and_descriptions("data/TGDD/laptop.csv", "data/TGDD/description.csv")

print(docs[1].page_content)
print(docs[1].metadata)


