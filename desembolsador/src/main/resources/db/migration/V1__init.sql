CREATE TABLE disbursement_batch (
    id VARCHAR(36) PRIMARY KEY,
    client_code VARCHAR(100),
    status VARCHAR(50),
    scheduled_at TIMESTAMP
);

CREATE TABLE disbursement_step (
    id VARCHAR(36) PRIMARY KEY,
    type VARCHAR(50),
    status VARCHAR(50),
    amount DECIMAL(18,2),
    client_request_id VARCHAR(100),
    batch_id VARCHAR(36),
    error_code VARCHAR(20),
    error_message VARCHAR(255),
    CONSTRAINT fk_batch FOREIGN KEY(batch_id) REFERENCES disbursement_batch(id),
    CONSTRAINT ux_client_request_id UNIQUE(client_request_id)
);
