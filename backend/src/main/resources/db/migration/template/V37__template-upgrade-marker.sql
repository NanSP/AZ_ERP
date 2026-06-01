CREATE TABLE public.template_upgrade_markers (
    id BIGSERIAL PRIMARY KEY,
    version_marker VARCHAR(20) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO public.template_upgrade_markers (version_marker)
VALUES ('V37');
