-- Assinaturas Mercado Pago (plano Pro)

CREATE TABLE subscriptions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id),
    mp_preapproval_id VARCHAR(64) NOT NULL,
    mp_preapproval_plan_id VARCHAR(64),
    status VARCHAR(32) NOT NULL,
    checkout_url TEXT,
    payer_email VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_subscriptions_mp_preapproval_id UNIQUE (mp_preapproval_id)
);

CREATE INDEX idx_subscriptions_user_id ON subscriptions (user_id);
