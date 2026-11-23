-- 清理孤儿订单日志：订单已删除，或日志时间早于订单创建时间（旧表重置后 id 复用导致错位）
DELETE l FROM order_logs l LEFT JOIN orders o ON l.order_id = o.id
WHERE o.id IS NULL OR l.created_at < o.create_time;
