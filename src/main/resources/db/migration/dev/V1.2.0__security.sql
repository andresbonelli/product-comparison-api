CREATE TABLE IF NOT EXISTS api_key (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          key_value VARCHAR(255) UNIQUE NOT NULL,
                          role VARCHAR(50) NOT NULL, -- 'ROOT', 'USER', etc
                          expires_at TIMESTAMP NULL,
                          is_active BOOLEAN DEFAULT TRUE
);