-- Enable pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Spring AI pgvector store table
CREATE TABLE IF NOT EXISTS vector_store (
    id        UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    content   TEXT,
    metadata  JSONB,
    embedding VECTOR(1536)
);

-- HNSW index for fast cosine similarity search
CREATE INDEX IF NOT EXISTS vector_store_embedding_idx
    ON vector_store USING hnsw (embedding vector_cosine_ops);
