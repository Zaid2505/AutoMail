AI-Powered Autonomous Email Support Agent

Built the foundation for a fully autonomous AI-powered email support agent using Spring Boot, Spring AI, MCP (Model Context Protocol), and Mailpit.

Introduced the AI Agent Loop: Reason → Act → Observe → Repeat, enabling the agent to autonomously work toward completing customer requests.
Built a Support Agent application using Spring Boot, Spring AI, and MCP Client that polls the support mailbox every 10 seconds and passes incoming emails to the LLM for processing.
Developed an MCP Server that provides controlled tools for accessing customer/order data and performing actions such as issuing refunds and creating support tickets.
Kept direct database access away from the LLM, with MCP acting as a controlled layer for validation, guardrails, business rules, and auditing.
Implemented the complete workflow: receive email → investigate → take action → observe results → repeat until complete → generate and send response.
Used Mailpit as a fake/local email server to safely test email receiving and SMTP-based responses without sending real emails.
