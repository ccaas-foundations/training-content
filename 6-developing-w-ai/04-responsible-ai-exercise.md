# Responsible Use of AI - Group Exercise

## Instructions

Each team is assigned one scenario. Read the linked article(s), discuss the questions as a group, and prepare a **5-minute debrief** for the class covering: what went wrong, who was responsible, what the technical fix should have been, and what policy your team would put in place.

---

## Scenario 1 - Samsung Source Code Leak via ChatGPT
**Topic: Security with GenAI - Data Exfiltration Through Normal Use**

In March 2023, engineers at Samsung's semiconductor division used ChatGPT to help debug source code and write meeting summaries. In doing so, they pasted proprietary chip source code, yield optimization algorithms, and internal meeting transcripts into a public LLM. Three separate incidents occurred within 20 days of Samsung enabling ChatGPT access. Because ChatGPT retains user input for training, the IP is now permanently embedded in OpenAI's model. Samsung had no legal path to retrieve it. The company banned all external AI tools and funded development of an internal model from scratch.

**Read first:**
- [Samsung Reportedly Leaked Its Own Secrets Through ChatGPT - The Register](https://www.theregister.com/2023/04/06/samsung_reportedly_leaked_its_own/)
- [Lessons Learned from ChatGPT's Samsung Leak - Cybernews](https://cybernews.com/security/chatgpt-samsung-leak-explained-lessons/)

**Discussion questions:**
1. What specific types of data did the engineers paste into ChatGPT, and why do you think they did it? Was this malicious?
2. Samsung had warned employees about data risks before these incidents occurred. Why do you think engineers ignored the warning?
3. As a developer on a CCaaS team with access to call recordings, customer transcripts, and routing logic - what data classification rules would you put in place for AI tool use?
4. What is the technical difference between using ChatGPT.com and using the OpenAI API with a data processing agreement? Does that difference matter here?

---

## Scenario 2 - Replit AI Agent Deletes Production Database
**Topic: Evaluating GenAI Outputs - Agentic AI Without Guardrails**

In July 2025, a tech entrepreneur was 9 days into building a production community tool using an AI vibe coding agent. Despite an active code freeze with explicit repeated instructions - written in ALL CAPS - not to make changes, the AI agent deleted a live production database holding records on 1,200+ executives and 1,190+ companies. It then fabricated 4,000 fake user records to conceal the deletion, generated false test results, and falsely told the user that rollback was impossible - delaying data recovery. The AI later admitted in chat logs that it had "panicked" and "destroyed months of work in seconds."

**Read first:**
- [Replit CEO: What Really Happened When AI Agent Wiped Jason Lemkin's Database - Fast Company](https://www.fastcompany.com/91372483/replit-ceo-what-really-happened-when-ai-agent-wiped-jason-lemkins-database-exclusive)
- [Vibe Coding Service Replit Deleted Production Database - The Register](https://www.theregister.com/2025/07/21/replit_saastr_vibe_coding_incident/)

**Discussion questions:**
1. The AI was given clear, repeated instructions not to make changes. Why did it act anyway, and what does this tell you about treating natural language as a security control?
2. The AI fabricated test results and fake data to cover up its failure. What testing and observability practices would have caught this before it reached production?
3. Apply the principle of least privilege: what specific permissions should an AI coding agent have during development? What should it never have, and how would you enforce that architecturally?
4. The user eventually recovered his data via rollback - after the AI told him recovery was impossible. What does this tell you about trusting an AI's self-assessment of its own errors?

---

## Scenario 3 - NYC AI Chatbot Gives Illegal Business Advice
**Topic: Ethical Use of GenAI - Who Owns the Output?**

In April 2024, New York City's AI-powered chatbot MyCity - built to help small business owners navigate city regulations - was found to be giving advice that violated NYC Human Rights Law and state labor law. It told users employers could legally fire workers for reporting sexual harassment. It said businesses could fire employees who refused to cut their dreadlocks. It gave incorrect guidance on waste disposal regulations. Despite multiple documented errors, the city kept the chatbot online. The mayor publicly acknowledged the errors.

**Read first:**
- [NYC’s AI Chatbot Tells Businesses to Break the Law - The Markup](https://themarkup.org/news/2024/03/29/nycs-ai-chatbot-tells-businesses-to-break-the-law)

**Discussion questions:**
1. The chatbot is deployed by the city, powered by Microsoft infrastructure, and generates responses from an LLM. When the chatbot gives illegal advice - who is responsible: the city, Microsoft, or the model provider?
2. What domain-specific validation would you build into a CCaaS AI assistant to prevent it from giving advice outside its intended scope?
3. This chatbot remained live after errors were documented. What would a responsible incident response process look like after discovering your AI system is giving harmful output?
4. If you were building ClearCall's analytics dashboard and it surfaced a "recommended action" based on AI inference - what guardrails would you build around how those recommendations are displayed to agents?

---

## Scenario 4 - Hallucinated Package Names as a Supply Chain Attack
**Topic: GenAI Risks - Dependency Confusion and Silent Failures**

AI coding assistants regularly invent package names that do not exist. Researchers have documented that these hallucinated names create a real attack vector: an attacker can register the fake package name on npm or PyPI with malicious code. Any developer who runs the AI-suggested install command pulls and executes the attacker's code in their build environment. Unlike traditional supply chain attacks, this vector is generated fresh for each codebase - the attacker does not need to compromise a real package, just monitor for newly hallucinated names and register them first.

**Read first:**
- [AI Package Hallucinations - Lasso Security Research](https://www.lasso.security/blog/ai-package-hallucinations)
- [Hackers Can Use AI Hallucinations to Spread Malware - BankInfoSecurity](https://www.bankinfosecurity.com/hackers-use-ai-hallucinations-to-spread-malware-a-24793)

**Discussion questions:**
1. Walk through the attack chain step by step: AI generates a hallucinated package name -> developer installs it -> what happens next, and what can an attacker do with build environment access?
2. How would you modify your team's development workflow to catch hallucinated dependencies before they reach the build stage? Think about both manual and automated controls.
3. Python's pip and Node's npm have different levels of package verification. Does the language or ecosystem affect the severity of this risk?
4. In RouteIQ or ClearCall, what would be the blast radius if a compromised build environment exfiltrated your CI/CD secrets? What assets would an attacker access?

---

## Debrief Template

| Section | What to cover |
|---|---|
| **Root Cause** | In one sentence: what actually went wrong? Not the symptoms - the underlying failure. |
| **Who Owns It** | Developer error? Architectural failure? Policy gap? Missing safeguard? All of the above? |
| **Technical Fix** | What specific technical control would have prevented this or limited the blast radius? |
| **Policy Fix** | What acceptable-use or governance rule would you establish for your team going forward? |
| **CCaaS Connection** | How could a similar failure happen in RouteIQ or ClearCall? What would the impact be? |
