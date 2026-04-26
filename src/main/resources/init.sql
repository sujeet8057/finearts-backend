-- Create database
CREATE DATABASE IF NOT EXISTS fineartsDB;

-- Use the database
\c fineartsDB;

-- Create Admin table
CREATE TABLE IF NOT EXISTS admins (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Create Product table
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE PRECISION NOT NULL,
    image_url VARCHAR(255),
    image_path VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create Contact table
CREATE TABLE IF NOT EXISTS contacts (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create Newsletter Subscribers table
CREATE TABLE IF NOT EXISTS newsletter_subscribers (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    subscribed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_admins_email ON admins(email);
CREATE INDEX idx_products_is_active ON products(is_active);
CREATE INDEX idx_contacts_email ON contacts(email);
CREATE INDEX idx_newsletter_email ON newsletter_subscribers(email);

-- Insert default admin user
-- Email: admin@sonifinearts.com
-- Password: Admin@123 (BCrypt hash)
INSERT INTO admins (email, password, name, is_active)
VALUES (
    'admin@sonifinearts.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/nP2',
    'Admin',
    TRUE
) ON CONFLICT (email) DO NOTHING;

-- Insert sample products
INSERT INTO products (name, description, price, is_active)
VALUES
    ('Shiva Statue', 'Beautiful handcrafted Shiva statue', 5999.00, TRUE),
    ('Krishna Idol', 'Traditional Krishna idol with flute', 4999.00, TRUE),
    ('Ganesh Murti', 'Blessed Ganesh statue', 3999.00, TRUE),
    ('Durga Idol', 'Powerful Durga Mata statue', 6999.00, TRUE)
ON CONFLICT DO NOTHING;

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_admins_timestamp
    BEFORE UPDATE ON admins
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER update_products_timestamp
    BEFORE UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- Display success message
SELECT 'Database initialized successfully!' as message;
