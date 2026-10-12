-- ==============================================================================
-- Jenkins-Based Policy Renewal Reminder System
-- Initial Synthetic Seed Data
-- Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
-- ==============================================================================

INSERT INTO POLICY (POLICY_NUMBER, CUSTOMER_NAME, CUSTOMER_EMAIL, POLICY_TYPE, START_DATE, EXPIRY_DATE, PREMIUM_AMOUNT, RENEWAL_STATUS, NOTES, CREATED_AT, UPDATED_AT)
VALUES 
('POL-1001', 'Ahmed Ali', 'ahmed.ali@example.com', 'Health', '2025-11-01', '2026-10-22', 7500.00, NULL, 'Standard Comprehensive Health Cover. Renewal reminder pending.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('POL-1002', 'Priya Sharma', 'priya.sharma@example.com', 'Vehicle', '2025-10-28', '2026-10-28', 4200.50, NULL, 'Comprehensive Motor Insurance (Four Wheeler). Priority contact requested.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('POL-1003', 'Rahul Verma', 'rahul.verma@example.com', 'Life', '2025-09-01', '2026-09-30', 12000.00, NULL, 'Term Life Insurance 25-yr coverage. Grace period check.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('POL-1004', 'Fatima Khan', 'fatima.khan@example.com', 'Home', '2025-05-15', '2026-05-14', 6800.00, 'RENEWED', 'Residential Property & Contents. Renewed for next financial cycle.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('POL-1005', 'James Thomas', 'james.thomas@example.com', 'Travel', '2026-08-01', '2026-10-18', 2150.00, NULL, 'International Multi-trip travel shield. Expiring in under 10 days.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('POL-1006', 'Ananya Patel', 'ananya.patel@example.com', 'Health', '2026-01-15', '2027-01-14', 9800.00, NULL, 'Family Floater Health Plan. Premium paid annually via direct debit.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('POL-1007', 'Vikram Malhotra', 'vikram.m@example.com', 'Vehicle', '2025-08-10', '2026-08-09', 3450.00, NULL, 'Two-wheeler comprehensive policy. Expired policy.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('POL-1008', 'Sneha Kulkarni', 'sneha.k@example.com', 'Life', '2026-03-01', '2027-02-28', 15000.00, NULL, 'Endowment Assurance with accidental death benefit rider.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('POL-1009', 'Rohan Deshmukh', 'rohan.d@example.com', 'Vehicle', '2025-11-05', '2026-11-04', 5100.00, NULL, 'Commercial Vehicle policy. Due for renewal within 25 days.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('POL-1010', 'Zoya Merchant', 'zoya.m@example.com', 'Home', '2025-10-01', '2026-10-01', 8200.00, 'RENEWED', 'Luxury villa cover. Renewal payment verified and policy extended.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
