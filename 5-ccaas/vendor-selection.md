# Exercise - Vendor Selection

You have been brought in as a consultant. Your client needs a CCaaS platform. Based on each brief your group should recommend either **Amazon Connect** or **Google Customer Engagement Suite** - and be able to defend that recommendation.

#### For Each Brief Discuss With Your Breakout Group

**1. Your recommendation**
Which platform, Connect or Google CES.

**2. The two strongest arguments for your choice**
Reference details from the brief.

**3. The biggest risk or tradeoff**
What does your recommended platform not do as well? What would you tell the client to watch out for? A recommendation without acknowledged tradeoffs is not credible.

**4. One question you'd need answered before finalizing**
What information is missing from this brief that would materially affect your recommendation?

---

## The briefs

---

### Group 1 - Meridian Energy

Meridian is a regional electric and gas utility serving 600,000 residential and 40,000 business customers across four states in the Pacific Northwest. They currently run an on-premise contact center with 280 agents using hardware that is over eight years old. The vendor who built the system no longer supports it.

Their contact center handles outage reporting, billing inquiries, service requests, and emergency line calls. During major weather events - which happen three to four times per year - inbound call volume spikes from a normal 2,000 calls per day to over 15,000 calls in a single hour. Their current system can handle 200 concurrent calls. During the last major ice storm, over 40% of callers abandoned before reaching anyone.

Meridian's IT organization runs almost entirely on AWS. Their customer data lives in Salesforce. They have one developer dedicated to contact center systems and are hoping to keep it that way. They have no current AI capabilities and are not looking to invest heavily in AI in the first phase - they just need a stable, scalable platform that doesn't fall over during outages.

Their primary success metrics: reduce abandoned calls during peak events, improve self-service containment for routine billing inquiries, and get the migration done within six months.

---

### Group 2 - Crestline Financial

Crestline is a mid-size consumer bank with 3.2 million retail customers and a contact center of 1,400 agents across three locations in the US and one in the Philippines. They handle credit card support, mortgage inquiries, fraud alerts, and general account management.

Crestline is currently running on a Genesys on-premise system and has been for twelve years. They are not planning to migrate - they are evaluating adding an AI layer on top of their existing routing infrastructure to improve self-service and agent productivity. They are not an AWS shop; their cloud infrastructure is split between Google Cloud and Azure.

Their biggest operational challenge is after-call work. Agents spend an average of four minutes on documentation after every interaction, which at their call volume represents significant payroll cost. Their second challenge is new agent ramp time - it currently takes three months before a new agent reaches acceptable handle time metrics, and attrition means they are constantly hiring.

They are also under active scrutiny from their compliance team around call recording and PII - any AI solution needs to be deployed in a way that keeps customer financial data within defined boundaries.

Their primary success metrics: reduce after-call work time, reduce new agent ramp time, maintain compliance with financial services data regulations.

---

### Group 3 - Parcel Now

Parcel Now is a last-mile delivery platform operating in 22 countries across North America, Europe, and Southeast Asia. They have three distinct customer populations: consumers who ordered packages, merchants who ship packages, and delivery drivers who are independent contractors. All three contact the support center, often about the same delivery, with very different questions and needs.

Their contact center handles 800,000 contacts per day. Approximately 70% of those are from delivery drivers calling while on route - asking about addresses, reporting problems with deliveries, requesting support with the app. These callers need fast, accurate answers with minimal latency. They are driving. They cannot navigate a complex menu.

Parcel Now currently uses Amazon Connect for routing but has reached the limits of what their existing IVR handles. The majority of driver calls still route to human agents because the IVR cannot understand the variety of ways drivers describe their problems. They want to implement conversational AI that can handle driver calls end to end for the most common issue types.

Language coverage is a significant constraint - drivers in Southeast Asia speak a combined twelve languages. Their current system supports English only.

Their primary success metrics: increase IVR containment for driver calls from current 30% to 60%+, expand language support to at least eight languages, reduce driver time on the phone.

---

### Group 4 - Lumière

Lumière is a luxury skincare and fragrance brand operating in 38 countries with an average transaction value of $340. Their contact center has 90 agents and handles around 4,000 contacts per day across phone, email, chat, and WhatsApp. Their customer base expects a concierge-level experience - highly personalized, remembers past interactions, never feels transactional.

Lumière's current contact center is a mix of tools that do not talk to each other well. Agents switch between three different systems during a call to find order history, loyalty points, and product knowledge. Average handle time is 8.5 minutes, which is high, and CSAT scores have been declining for two years.

Their CTO has mandated a full contact center modernization. Lumière runs on Google Cloud. Their customer data lives in a custom-built CRM that exposes a REST API. They have a small but capable engineering team of six developers. They are interested in AI but want to be thoughtful - their brand depends on the interaction feeling human, and they are concerned about AI that feels robotic or generic.

Their primary success metrics: reduce agent handle time, improve CSAT, deliver a more personalized agent experience, and maintain the brand feel of every interaction.
