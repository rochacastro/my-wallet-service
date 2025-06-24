delete from wallet_history;
delete from wallet;
delete from users;

INSERT INTO users (id, name, cpf) VALUES
  ('a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a1a1', 'João Silva', '27175250096'),
  ('b2b2b2b2-b2b2-b2b2-b2b2-b2b2b2b2b2b2', 'Maria Oliveira', '26936761003');

INSERT INTO wallet (id, user_id, amount) VALUES
  ('3d6e8312-8a0d-45c1-abc1-111111111111', 'a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a1a1', 1000.00),
  ('3d6e8312-8a0d-45c1-abc1-222222222222', 'b2b2b2b2-b2b2-b2b2-b2b2-b2b2b2b2b2b2', 500.00);

INSERT INTO wallet_history (id, wallet_id, amount, transaction_time) VALUES
  ('c53da836-6483-42ba-9978-b81c9b4b6d3a', '3d6e8312-8a0d-45c1-abc1-111111111111', 1000.00, '2025-06-22T10:00'),
  ('c191f54b-5fce-403b-b579-5b21a72ad5e0', '3d6e8312-8a0d-45c1-abc1-111111111111', 421.79, '2025-06-21T10:00'),
  ('a3cc185a-3c1c-4a32-a414-12f0c6244fc4', '3d6e8312-8a0d-45c1-abc1-111111111111', 5231.30, '2025-06-20T10:00'),
  ('1e6eb8a7-0563-40d9-a905-da70f3aed613', '3d6e8312-8a0d-45c1-abc1-111111111111', 10.00, '2025-06-19T10:00'),
  ('5cac87f2-a918-48dd-8f56-c46258a1baec', '3d6e8312-8a0d-45c1-abc1-222222222222', 500.00, '2025-06-22T10:00');