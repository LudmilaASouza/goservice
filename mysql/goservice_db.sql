CREATE DATABASE goservice_db;

SHOW TABLES;

SELECT * FROM agendamento;
DESC agendamento;
UPDATE agendamento SET status_agendamento = "CONFIRMADO" 
WHERE id = 1;


SELECT * FROM usuarios;
DESC usuarios;

SELECT * FROM servicos;
DESC servicos;

DROP TABLE prestadores_servico;

SELECT * FROM prestadores_servicos;
DESC prestadores_servicos;

INSERT INTO prestadores_servicos (servico_id, prestador_id)
VALUES
    (6, 5),
    (5, 5),
    (7, 5);


