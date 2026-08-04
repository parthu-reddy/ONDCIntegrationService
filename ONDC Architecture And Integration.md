# **Architecting a Decentralized Food Delivery Platform: A Comprehensive Blueprint for ONDC Integration in Bengaluru**

## **1\. The Open Network for Digital Commerce (ONDC) Paradigm**

The Open Network for Digital Commerce (ONDC) represents a foundational structural shift in the global e-commerce architecture, transitioning the industry from a platform-centric monopoly model to an interoperable, decentralized network. Initiated by the Department for Promotion of Industry and Internal Trade (DPIIT), ONDC utilizes the open-source Beckn Protocol to unbundle the traditional e-commerce supply chain1. Instead of a single application operating as a walled garden that controls seller onboarding, customer acquisition, logistics dispatch, and payment settlements, ONDC fragments these responsibilities across distinct network entities. These entities include Buyer Applications (BAPs), Seller Applications (BPPs), and Logistics Providers, all communicating via a standardized network extension layer1.  
Implementing a Food and Beverage (F\&B) delivery application in Bengaluru, India, provides a unique set of operational challenges and opportunities. As a hyper-dense, technology-forward metropolis, Bengaluru demands sub-second geospatial routing, robust failover mechanisms for high-volume transactions, and stringent localized infrastructure compliance. This report provides a highly exhaustive, production-ready architectural and operational blueprint for integrating a full-stack F\&B delivery platform into the ONDC ecosystem, specifically targeting the ONDC:RET11 domain specifications for food delivery and the Logistics specifications for hyperlocal dispatch4. The architecture mandates rigorous adherence to cryptographic security, dynamic asynchronous transaction flows, automated catalog management, and seamless financial reconciliation.

## **2\. Cryptographic Infrastructure and Network Onboarding**

Participation in the ONDC network requires strict adherence to cryptographic security standards to ensure that every decentralized HTTP request is legally binding, non-repudiable, and secure from unauthorized tampering. Network Participants (NPs) must undergo a rigorous onboarding process on the Staging, Pre-Production, and Production registries.

### **2.1 Domain Configuration and Key Generation**

The foundation of ONDC identity is irreversibly tied to a Fully Qualified Domain Name (FQDN). All communications with the ONDC registry and other network participants must originate from this specific domain. Furthermore, this domain must be secured by a valid SSL certificate, which the registry utilizes for Online Certificate Status Protocol (OCSP) validation during the onboarding phase5.  
Network Participants must generate two distinct cryptographic key pairs. These keys are typically generated using the Libsodium library, or native cryptography modules in runtime environments such as Node.js or Java, utilizing providers like BouncyCastle for robust elliptic curve implementations5.

| Key Type | Algorithm | Format | Primary Function in ONDC |
| :---- | :---- | :---- | :---- |
| **Signing Key Pair** | Ed25519 | Base64 Encoded | Guarantees message authenticity and non-repudiation for all API requests and callbacks5. |
| **Encryption Key Pair** | X25519 | ASN.1 DER (Public), Base64 (Private) | Encrypts sensitive payloads and facilitates the decryption of the registry subscription challenge5. |

### **2.2 The Subscription Payload and Site Verification**

To cryptographically verify domain ownership, the ONDC registry employs a highly automated site verification mechanism. The system must first generate a unique request\_id, which can be formatted as a UUID or an alphanumeric string. This string must be signed using the Ed25519 signing\_private\_key without applying any preceding hashing algorithm, resulting in a SIGNED\_UNIQUE\_REQ\_ID5.  
The application infrastructure must host a static HTML file directly at the root of the subscriber domain (e.g., https://\<subscriber\_id\>/ondc-site-verification.html). This file must contain the generated signature within a specific HTML meta tag, allowing the ONDC registry to scrape and verify the domain upon request5. Following the deployment of this verification file, the backend submits a /subscribe POST request to the respective registry (e.g., https://prod.registry.ondc.org/subscribe). The payload must explicitly define the subscriber\_id, the Base64-encoded public keys, a unique\_key\_id for key versioning, and the operational role of the application. Roles are defined by ops\_no, where 1 designates a Buyer App, 2 designates a Seller App, and 4 designates a unified Buyer and Seller App5.

### **2.3 The AES Decryption Challenge**

Upon receiving the subscription payload, the ONDC registry verifies the SSL certificate and the HTML meta tag. If these automated checks are successful, the registry triggers an asynchronous callback to the application's exposed /on\_subscribe endpoint5. This callback contains an encrypted JSON payload featuring a challenge string.  
The application must dynamically compute a shared secret using its own X25519 encryption\_private\_key and the environment-specific ONDC public key. For the Production environment, the ONDC public key is defined as MCowBQYDK2VuAyEAvVEyZY91O2yV8w8/CAwVDAnqIZDJJUPdLUUKwLo3K0M=5. Utilizing this shared secret, the backend decrypts the challenge string via the Advanced Encryption Standard (AES) algorithm and responds synchronously with the decrypted string5. Successful decryption results in an ACK status from the registry, formalizing the application's active status within the network. The system can then query the secure /v2.0/lookup API to confirm its operational presence and propagate its public keys to other network nodes5.

## **3\. Request Authentication and Header Signatures**

Because ONDC operates as a decentralized mesh rather than a centralized server, every individual HTTP request between a Buyer App and a Seller App represents a legally binding commercial contract. ONDC mandates the use of digital signatures in the Authorization header for direct requests, and the X-Gateway-Authorization header for requests routed through a Beckn Gateway (BG)8.

### **3.1 Constructing the Cryptographic Signature**

To authorize a request, the sending backend must perform a series of deterministic operations. First, the system computes a BLAKE-512 (or BLAKE2b-512) hash of the entire raw HTTP request body to create a digest string8. Next, the system generates integer Unix timestamps for the (created) time and the (expires) time. Signatures with expiration times falling in the past will be categorically rejected by the receiving node to prevent replay attacks8.  
The system then constructs a signing string by concatenating these elements strictly using newline characters, formatting it as (created): \[timestamp\]\\n(expires): \[timestamp\]\\ndigest: BLAKE-512=\[hash\]8. The Network Participant signs this exact string using its registered Ed25519 private key and encodes the resulting byte array into a Base64 string7.

### **3.2 The Authorization Header Validation**

The resulting signature is packaged into the HTTP header alongside a keyId that allows the receiving node to perform a registry lookup. The keyId string concatenates the subscriber\_id, the unique\_key\_id, and the signature algorithm, delimited by pipe characters8.  
When a receiver accepts this payload, it extracts the keyId, queries the ONDC /v2.0/lookup directory to retrieve the sender's public key (or retrieves it from a local Redis cache), and verifies both the BLAKE-512 hash integrity and the Ed25519 signature validity5. This zero-trust architecture ensures that malicious actors cannot spoof orders, alter payload contexts, or manipulate pricing data while the request is in transit over the open internet.

## **4\. The ONDC RET11 Transaction Lifecycle and API Specifications**

The ONDC Retail Specifications for Food and Beverage (ONDC:RET11) dictate a highly structured, multi-step asynchronous flow4. A standard order traverses through specific API phases, each requiring an immediate synchronous acknowledgment (ACK) followed by a subsequent asynchronous callback (on\_\<action\>) containing the requested data.

### **4.1 Discovery Phase (search and on\_search)**

The transaction lifecycle initiates when a consumer opens a Buyer Application and queries for food. The BAP broadcasts a /search intent across the network. The BPP receives this webhook, filters its database for active, serviceable outlets based on the provided geolocation, and returns the complete hierarchical menu via the /on\_search callback12. This is classified as Flow 1, enabling a full catalog refresh. To reduce payload sizes and bandwidth consumption, ONDC also supports /search\_inc and /on\_search\_inc endpoints to push only real-time delta updates, such as toggling an item out of stock or adjusting a price dynamically12. Furthermore, Flow 8 facilitates catalog discovery and window shopping without progressing to order intent12.

### **4.2 Order Quotation Phase (select and on\_select)**

Once a consumer adds items to their cart, the BAP sends a /select request targeting specific item IDs. The BPP evaluates the request, validates current inventory levels, calculates localized taxes (such as GST), applies packaging charges, and determines delivery fees. It returns a comprehensive financial breakdown via the /on\_select callback12. If an item has sold out since the initial search phase, the BPP handles this via Flow 3 (Out of Stock Recovery), returning an error or alternative suggestion, allowing the buyer to reselect items before proceeding12.

### **4.3 Checkout and Confirmation Phase (init and confirm)**

During the initialization phase (/init and /on\_init), the BAP provides the final billing details, delivery address, and KYC data of the consumer. The BPP reserves the required kitchen inventory and locks the final financial quote12. Upon consumer payment on the Buyer App, the BAP sends the /confirm request, generating a legally binding order contract. The BPP accepts the order, generating an internal order ID and returning the active fulfillment state in the /on\_confirm payload12.

### **4.4 Flow-Based Validation and Special Business Scenarios**

The ONDC architecture defines specific flow IDs to handle complex e-commerce scenarios. Validation utilities enforce strict payload schema compliance for these distinct flows12.

| Flow ID | Scenario Description | API Sequence Characteristics |
| :---- | :---- | :---- |
| **Flow 2** | Complete Order with Delivery | Standard sequence from search through confirm, followed by all progressive on\_status updates until on\_track and delivery12. |
| **Flow 4** | Order Cancellation | Standard order placement followed by cancel and on\_cancel prior to fulfillment12. |
| **Flow 7** | Catalog Rejection | A BAP rejects a seller's catalog immediately after on\_search due to policy or quality non-compliance12. |
| **Flow 002** | Self-Pickup | Customer elects to collect the order. Fulfillment type is strictly Self-Pickup. The flow terminates at Order-picked-up12. |
| **Flow 005** | Force Cancel | A seller-initiated cancellation (e.g., a kitchen disaster) triggered after the order was already packed or picked up12. |
| **Flow 012** | Cash on Delivery (COD) | Payment type is set to ON-FULFILLMENT. Crucially, settlement details are provided in confirm rather than on\_init12. |
| **Flows 0091-0098** | Promotional Offer Flows | Handles discounts, Buy X Get Y, freebies, slab-based offers, combo offers, and financing offers12. |

### **4.5 Fulfillment State Machine and Telemetry (status and track)**

The ONDC:RET11 domain relies exclusively on Hyperlocal Point-to-Point (P2P) routing12. The BPP must proactively push updates to the BAP via the /on\_status API as the order physically progresses through the kitchen and logistics network.  
The mandated state machine for ONDC F\&B fulfillment is rigidly enforced12. Required states include Pending (order confirmed, processing in kitchen), Packed (items ready for dispatch), Order-picked-up (handed to delivery executive), and Order-delivered. Optional states provide finer granularity, including Agent-assigned, Out-for-pickup, At-pickup, and At-delivery12. Crucially, standard retail states such as In-transit, At-destination-hub, or Out-for-delivery are classified as forbidden states within the hyperlocal F\&B domain and will trigger severe compliance validation failures12.  
During the active delivery window, the /track and /on\_track APIs provide real-time telemetry. The response typically includes a URL for a tracking webview or a WebSocket endpoint allowing the consumer to observe the delivery executive's live GPS coordinates14.

## **5\. Certification and Log Validation**

Prior to deploying a platform on the production registry, Network Participants must undergo a rigorous technical certification process. The ONDC Log Validation Utility is a comprehensive tool designed to validate transaction logs across multiple domains, ensuring the accuracy and integrity of transaction data by validating API payloads against core specifications12.  
Participants generate transaction logs covering the required flows (e.g., Flows 1 through 9 for version 1.2.0, and additional special flows for version 1.2.5)12. These logs are submitted to the ONDC GitHub repository (e.g., v1.2.0-logs) where automated GitHub Actions and ONDC technical teams verify structural and cryptographic compliance15. To facilitate this, ONDC provides the Pramaan platform, featuring tools like the RSF Mock Server and Sandbox environments to validate integration without requiring live counter-parties4. Furthermore, ONDC offers white-label reference implementations for Buyer, Seller, and Logistics applications to accelerate the development lifecycle16. Automated testing can also be managed using the ONIX mock runner, which provides a Node.js and browser-compatible sandbox with Zod-based configuration validation for multi-step flow management17.

## **6\. Seller Application Architecture and Entity Modeling**

Acting as an ONDC Seller Node (BPP) for a food delivery network necessitates a robust, multi-tenant architectural design. Treating all restaurants as flat, isolated entities will cause catastrophic failures in legal compliance, payout routing, and geospatial logic18. A production-ready architecture requires a strict parent-child hierarchical database schema, optimally deployed on PostgreSQL.

### **6.1 Brand (Parent Entity) vs. Outlet (Child Entity)**

The database must meticulously decouple the overarching corporate brand from its physical kitchens.

| Entity Level | PostgreSQL Table | Critical Attributes | ONDC Operational Purpose |
| :---- | :---- | :---- | :---- |
| **Brand (Parent)** | brands | GSTIN, PAN, Verified Bank Account (IFSC, Account Number). | Manages corporate legal identity. Financial payouts, settlement reconciliation, and indirect tax collection (GST) are settled exclusively at this level18. |
| **Outlet (Child)** | outlets | 14-digit FSSAI License, PostGIS Coordinates, Operating Hours. | Manages physical fulfillment. Food safety certification and exact turn-by-turn logistics dispatch are bound strictly to the physical kitchen location18. |

### **6.2 Automated Identity and Document Verification**

During the merchant onboarding process, manual verification of business documents introduces operational bottlenecks and exposes the platform to fraud. The standard onboarding timeline ranges from two to five business days19. However, integrating with third-party automated KYC APIs (such as Signzy, Karza, or OnGrid) allows the platform to validate credentials synchronously, vastly accelerating time-to-market18.  
The Food Safety and Standards Authority of India (FSSAI) legally mandates an active 14-digit license for every premise preparing or selling food. The system must verify the active status of this specific license against government databases before allowing the outlet to broadcast its catalog to the ONDC network18. Furthermore, to prevent failed weekly wage settlements, the backend must execute an IMPS "Penny Drop" verification. The API instantly deposits ₹1.00 into the corporate bank account, returning the exact registered beneficiary name18. The backend matches this name against the PAN/GSTIN legal entity name to ensure strict anti-money laundering (AML) compliance and eliminate frustrating payout reversal cycles18.

## **7\. Comprehensive Food and Beverage Taxonomy Management**

Catalog management represents a significant operational challenge in food delivery. Conversion rates are heavily dependent on visual appeal and logical categorization. The system must pre-configure an exhaustive taxonomy of Indian and International cuisines to map effectively to the ONDC catalog specifications14.

### **7.1 Hybrid Asset Architecture (WebP and SVG)**

The frontend application should utilize a hybrid approach to asset rendering to balance performance with visual fidelity18. Menu items require photorealism; thus, default generic menu images must use highly compressed WebP formats. The backend dynamically assigns these fallback WebP images (e.g., delivering a high-quality photo of a dosa when the keyword "dosa" is detected) when restaurant operators fail to provide proprietary imagery18. Conversely, interface iconography, structural elements, and dietary markers (such as the ubiquitous green dot for vegetarian items) should utilize mathematical SVG vectors, which scale infinitely without pixelation and consume minimal bandwidth18.  
To manage the high volume of media traffic economically, the architecture must avoid traditional hyperscaler egress fees. Cloudflare R2 provides fully managed storage operating on a global edge network, caching images locally in Indian data centers while completely eliminating data egress fees, charging only a flat rate of $0.015 per GB per month18. For strict sovereign data localization compliance, Indian cloud providers like E2E Networks offer highly competitive S3-compatible object storage18.

### **7.2 Exhaustive Database Seeding: The Core F\&B Taxonomy**

To build a production-ready database schema for Bengaluru, the system must accommodate a vast array of localized and international items. The following tables provide an exhaustive master catalog of over 800 items, structured by cuisine and dietary preference, required to seed the menu database for accurate ONDC catalog syndication18.

#### **North Indian (Punjabi & Mughlai)**

| Dietary Type | Sub-Category | Item Seed List |
| :---- | :---- | :---- |
| **Vegetarian** | Curries | Paneer Butter Masala, Palak Paneer, Kadhai Paneer, Shahi Paneer, Matar Paneer, Malai Kofta, Chana Masala, Dal Makhani, Yellow Dal Tadka, Aloo Gobi, Bhindi Masala, Dum Aloo, Baingan Bharta, Mix Veg Curry, Mushroom Masala, Navratan Korma, Paneer Bhurji, Aloo Matar, Jeera Aloo, Rajma Masala, Kadhi Pakora, Methi Malai Matar, Pindi Chole, Lauki Kofta, Sarson Ka Saag, Aloo Capsicum, Vegetable Jalfrezi, Paneer Tikka Masala, Soya Chaap Masala, Tawa Paneer. |
| **Non-Veg** | Curries | Butter Chicken, Chicken Tikka Masala, Mutton Rogan Josh, Kadhai Chicken, Saag Chicken, Chicken Curry, Mutton Korma, Keema Matar, Chicken Do Pyaza, Egg Curry, Mutton Nihari, Chicken Rara, Handi Chicken, Mutton Masala, Fish Curry, Prawn Masala, Egg Bhurji, Chicken Changezi, Mutton Curry, Dhaba Style Chicken, Fish Tikka Masala, Mutton Keema, Chicken Bhuna, Mutton Bhuna, Chicken Afghani (Gravy), Lemon Chicken, Chicken Kali Mirch, Mutton Stew, Fish Amritsari Gravy, Egg Tikka Masala. |
| **Mixed** | Tandoor & Starters | Tandoori Chicken, Paneer Tikka, Malai Tikka, Hariyali Tikka, Achari Paneer Tikka, Chicken Seekh Kebab, Mutton Seekh Kebab, Hara Bhara Kebab, Fish Amritsari, Reshmi Kebab, Tangdi Kebab, Tandoori Soya Chaap, Malai Soya Chaap, Mushroom Tikka, Afghani Chicken, Galouti Kebab, Boti Kebab, Kalmi Kebab, Tandoori Aloo, Paneer Malai Tikka, Chicken Banjara Kebab, Mutton Boti Kebab, Tandoori Prawns, Fish Tikka, Veg Seekh Kebab. |
| **Vegetarian** | Breads & Sides | Butter Naan, Garlic Naan, Plain Naan, Tandoori Roti, Butter Roti, Lachha Paratha, Pudina Paratha, Aloo Kulcha, Paneer Kulcha, Onion Kulcha, Missi Roti, Rumali Roti, Keema Naan, Cheese Garlic Naan, Khasta Roti. |

#### **South Indian**

| Dietary Type | Sub-Category | Item Seed List |
| :---- | :---- | :---- |
| **Vegetarian** | Dosas | Masala Dosa, Plain Dosa, Rava Dosa, Onion Dosa, Paneer Dosa, Paper Dosa, Ghee Roast Dosa, Mysore Masala Dosa, Podi Dosa, Cheese Dosa, Set Dosa, Neer Dosa, Onion Rava Dosa, Rava Masala Dosa, MLA Pesarattu, Ghee Karam Dosa, Open Butter Dosa, Spring Dosa, Palak Dosa, Mushroom Dosa, Egg Dosa, Chicken Kheema Dosa, Mutton Kheema Dosa, Family Roast Dosa, Sponge Dosa. |
| **Vegetarian** | Idli, Vada & Tiffin | Plain Idli, Button Idli, Rava Idli, Thatte Idli, Kanchipuram Idli, Podi Idli, Fried Idli, Medu Vada, Dal Vada, Masala Vada, Maddur Vada, Onion Uttapam, Tomato Uttapam, Mix Veg Uttapam, Paneer Uttapam, Upma, Tomato Upma, Ven Pongal, Sweet Pongal, Kesari Bath, Bisi Bele Bath, Shavige Bath, Puliyogare (Tamarind Rice), Lemon Rice, Curd Rice. |
| **Vegetarian** | Curries & Meals | Sambar, Rasam, Aviyal, Poriyal, Kootu, Veg Stew, Olan, Kalan, Pachadi, Thoran, Veg Chettinad, Ennai Kathirikai, Mirchi Ka Salan, Gutti Vankaya Kura, Bendakaya Pulusu, Pappu, Tomato Pappu, Gongura Pappu, Cabbage Palya, Beans Palya, Chana Sundal, Veg Kurma, Veg Gassi, Pineapple Pachadi, Puli Inji. |
| **Non-Veg** | Curries & Breads | Chettinad Chicken, Malabar Fish Curry, Kerala Parotta, Appam, Idiyappam, Chicken Stew, Mutton Sukka, Chicken 65, Chicken Ghee Roast, Mutton Chukka, Prawn Ghee Roast, Meen Moilee, Fish Fry, Chicken Salna, Mutton Salna, Nattu Kozhi Kulambu, Andhra Chicken Curry, Gongura Mutton, Royyala Iguru (Prawns), Crab Masala, Chicken Kondattam, Karimeen Pollichathu, Mutton Pepper Fry, Egg Roast, Kallappam. |

#### **Biryani & Rice Dishes**

| Dietary Type | Sub-Category | Item Seed List |
| :---- | :---- | :---- |
| **Non-Veg** | Chicken Biryanis | Hyderabadi Chicken Dum Biryani, Lucknowi Chicken Biryani, Kolkata Chicken Biryani, Ambur Chicken Biryani, Dindigul Chicken Biryani, Thalassery Chicken Biryani, Donne Chicken Biryani, Chicken Tikka Biryani, Murgh Makhani Biryani, Tandoori Chicken Biryani, Chicken Fry Piece Biryani, Andhra Chicken Biryani, Chettinad Chicken Biryani, Malabar Chicken Biryani, Bombay Chicken Biryani, Sindhi Chicken Biryani, Memoni Chicken Biryani, Chicken 65 Biryani, Mughlai Chicken Biryani, Awadhi Chicken Biryani, Saffron Chicken Biryani, Chicken Keema Biryani, Handi Chicken Biryani, Bamboo Chicken Biryani, Matka Chicken Biryani. |
| **Non-Veg** | Mutton & Seafood | Hyderabadi Mutton Dum Biryani, Lucknowi Mutton Biryani, Kolkata Mutton Biryani, Ambur Mutton Biryani, Dindigul Mutton Biryani, Thalassery Mutton Biryani, Donne Mutton Biryani, Mutton Keema Biryani, Nalli Biryani, Mutton Fry Biryani, Mutton Mandi, Fish Biryani, Prawn Biryani, Crab Biryani, Seer Fish Biryani, Squid Biryani, Fish Tikka Biryani, Tandoori Fish Biryani, Mangalorean Prawn Biryani, Chettinad Mutton Biryani, Awadhi Mutton Biryani, Mutton Kofta Biryani, Egg Biryani, Double Egg Biryani, Omelette Biryani. |
| **Vegetarian** | Veg Biryanis | Veg Dum Biryani, Paneer Biryani, Mushroom Biryani, Soya Chaap Biryani, Aloo Biryani, Jackfruit (Kathal) Biryani, Mixed Veg Biryani, Paneer Tikka Biryani, Kaju Biryani, Baby Corn Biryani, Tandoori Veg Biryani, Chana Biryani, Rajma Biryani, Navratan Biryani, Palak Paneer Biryani, Cauliflower Biryani, Green Peas Biryani, Broccoli Biryani, Sweet Corn Biryani, Lotus Stem Biryani, Veg Hyderabadi Biryani, Veg Lucknowi Biryani, Sindhi Veg Biryani, Bamboo Veg Biryani, Matka Veg Biryani. |
| **Mixed** | Pulao & Rice Sides | Veg Pulao, Peas Pulao, Kashmiri Pulao, Tawa Pulao, Jeera Rice, Ghee Rice, Steam Rice, Curd Rice, Bagara Rice, Zafrani Pulao, Mushroom Pulao, Paneer Pulao, Soya Pulao, Mint Rice, Coriander Rice, Coconut Rice, Tomato Rice, Garlic Rice, Burnt Garlic Rice, Corn Pulao, Mixed Nut Pulao, Shahi Pulao, Moti Pulao, Yakhni Pulao, Zarda (Sweet Rice). |

#### **Indian Street Food & Chaat**

| Dietary Type | Sub-Category | Item Seed List |
| :---- | :---- | :---- |
| **Vegetarian** | Puri & Chaat | Pani Puri, Golgappa, Puchka, Dahi Puri, Sev Puri, Bhel Puri, Papdi Chaat, Aloo Chaat, Samosa Chaat, Kachori Chaat, Raj Kachori, Dahi Bhalla, Dahi Vada, Palak Patta Chaat, Tokri Chaat, Corn Chaat, Peanut Chaat, Chana Chaat, Matar Chaat, Aloo Tikki Chaat, Ragda Pattice, Ram Ladoo, Kanji Vada, Fruit Chaat, Sweet Potato Chaat, Aloo Handi Chaat, Kurkure Chaat, Makhana Chaat, Churmuri, Jhalmuri. |
| **Vegetarian** | Fried Snacks & Pakoras | Punjabi Samosa, Cocktail Samosa, Onion Kachori, Dal Kachori, Moong Dal Kachori, Bread Pakora, Onion Pakoda, Paneer Pakoda, Mirchi Bajji, Potato Bajji, Plantain Bajji, Spinach Pakoda, Gobi Pakoda, Mix Veg Pakoda, Soya Stick, Mathri, Namak Pare, Shakar Pare, Fafda, Jalebi, Khandvi, Dhokla, Khaman, Patra, Sabudana Vada. |
| **Mixed** | Pav & Bread-Based | Pav Bhaji, Cheese Pav Bhaji, Vada Pav, Cheese Vada Pav, Misal Pav, Usal Pav, Dabeli, Kutchi Dabeli, Kheema Pav, Bhurji Pav, Omelette Pav, Masala Pav, Bread Butter, Bun Maska, Bun Omelette, Bread Chole, Kulcha Chole, Matar Kulcha, Chole Bhature, Paneer Bhature. |
| **Mixed** | Rolls & Tawa Snacks | Veg Kathi Roll, Paneer Roll, Egg Roll, Chicken Roll, Mutton Roll, Soya Chaap Roll, Mushroom Roll, Noodle Roll, Manchurian Roll, Aloo Tikki, Paneer Tikki, Soya Tikki, Beetroot Tikki, Sabudana Tikki, Moong Dal Chilla, Besan Chilla, Paneer Chilla, Pesarattu, Tawa Sandwich, Bombay Sandwich, Grill Veg Sandwich, Toast Sandwich, Cheese Chilli Toast, Egg Frankie, Chicken Frankie. |

#### **Indo-Chinese (Desi Chinese)**

| Dietary Type | Sub-Category | Item Seed List |
| :---- | :---- | :---- |
| **Mixed** | Soups & Starters | Hot & Sour Veg Soup, Hot & Sour Chicken Soup, Sweet Corn Veg Soup, Sweet Corn Chicken Soup, Manchow Veg Soup, Manchow Chicken Soup, Lemon Coriander Soup, Clear Veg Soup, Clear Chicken Soup, Taluman Soup, Tom Yum Soup, Veg Spring Rolls, Chicken Spring Rolls, Honey Chilli Potato, Chilli Potato, Crispy Corn, Salt & Pepper Mushroom, Veg Manchurian Dry, Gobi Manchurian Dry, Paneer Chilli Dry, Mushroom Chilli Dry, Baby Corn Cigar, Thread Chicken, Chicken Lollipop, Dragon Chicken, Chicken 65, Chilli Chicken Dry, Pepper Chicken Dry, Drums of Heaven, Prawns Salt & Pepper. |
| **Mixed** | Dim Sums & Momos | Steamed Veg Momos, Fried Veg Momos, Pan-Fried Veg Momos, Kurkure Veg Momos, Tandoori Veg Momos, Steamed Paneer Momos, Fried Paneer Momos, Kurkure Paneer Momos, Steamed Chicken Momos, Fried Chicken Momos, Pan-Fried Chicken Momos, Kurkure Chicken Momos, Tandoori Chicken Momos, Cheese & Corn Momos, Mushroom Momos, Soya Momos, Schezwan Veg Momos, Schezwan Chicken Momos, Chilli Momo, Jhol Momo. |
| **Mixed** | Main Course Gravies | Veg Manchurian Gravy, Gobi Manchurian Gravy, Paneer Chilli Gravy, Mushroom Chilli Gravy, Veg Garlic Sauce, Veg Sweet & Sour, Veg Hot Garlic Sauce, Veg Schezwan Gravy, Chilli Chicken Gravy, Chicken Manchurian Gravy, Chicken Garlic Sauce, Chicken Hot Garlic, Chicken Sweet & Sour, Chicken Schezwan Gravy, Lemon Chicken Gravy, Orange Chicken, General Tso's Chicken, Mongolian Beef, Chilli Fish Gravy, Fish Manchurian, Fish Garlic Sauce, Chilli Prawns Gravy, Prawns Manchurian, Egg Chilli Gravy, Chicken Hong Kong Style. |
| **Mixed** | Noodles & Rice | Veg Hakka Noodles, Chicken Hakka Noodles, Egg Hakka Noodles, Prawn Hakka Noodles, Veg Schezwan Noodles, Chicken Schezwan Noodles, Chilli Garlic Noodles, Pan Fried Noodles, Veg American Chopsuey, Chicken American Chopsuey, Chinese Bhel, Veg Fried Rice, Chicken Fried Rice, Egg Fried Rice, Prawn Fried Rice, Veg Schezwan Fried Rice, Chicken Schezwan Fried Rice, Triple Schezwan Veg Rice, Triple Schezwan Chicken Rice, Burnt Garlic Fried Rice, Mushroom Fried Rice, Paneer Fried Rice, Singapore Fried Rice, Hong Kong Fried Rice, Mixed Meat Fried Rice. |

#### **Western Fast Food**

| Dietary Type | Sub-Category | Item Seed List |
| :---- | :---- | :---- |
| **Mixed** | Burgers | Veg Aloo Tikki Burger, Veg Cheese Burger, Paneer Burger, Veg Double Patty Burger, Spicy Veg Burger, Mushroom Burger, Black Bean Burger, Tofu Burger, Classic Chicken Burger, Crispy Chicken Burger, Grilled Chicken Burger, Chicken Cheese Burger, Chicken Double Patty, Spicy Chicken Burger, BBQ Chicken Burger, Zinger Burger, Fish Burger, Mutton Burger, Lamb Patty Burger, Egg Burger, Bacon Cheeseburger, Jalapeno Cheese Burger, Truffle Mushroom Burger, Teriyaki Chicken Burger, Pulled Chicken Burger. |
| **Mixed** | Pizzas & Sandwiches | Veg Club Sandwich, Chicken Club Sandwich, Grilled Cheese Sandwich, Veg Mayo Sandwich, Chicken Mayo Sandwich, Coleslaw Sandwich, Tuna Sandwich, Egg Salad Sandwich, BLT Sandwich, Ham & Cheese Sandwich, PB\&J Sandwich, Margherita Pizza (Fast Food Style), Cheese & Corn Pizza, Onion & Capsicum Pizza, Veggie Supreme Pizza, Chicken Sausage Pizza, Chicken Pepperoni Pizza, BBQ Chicken Pizza, Hawaiian Pizza, Meatballs Sub, Veggie Sub, Turkey Sub, Chicken Teriyaki Sub, Tuna Sub, Paneer Tikka Sub. |
| **Mixed** | Fried Chicken & Sides | Fried Chicken Bucket (Original), Fried Chicken Bucket (Spicy), Chicken Wings (BBQ), Chicken Wings (Buffalo), Chicken Wings (Garlic Parmesan), Chicken Nuggets, Veg Nuggets, Cheese Nuggets, Chicken Popcorn, Chicken Strips, Chicken Tenders, Classic French Fries, Peri Peri Fries, Cheese Fries, Loaded Chicken Fries, Sweet Potato Fries, Potato Wedges, Hash Browns, Onion Rings, Mozzarella Sticks, Jalapeno Poppers, Garlic Breadsticks, Cheese Breadsticks, Stuffed Breadsticks, Mac & Cheese Bites, Corn on the Cob, Mashed Potatoes, Gravy, Coleslaw, Biscuit. |
| **Mixed** | Wraps & Hot Dogs | Veg Wrap, Paneer Wrap, Chicken Wrap, Crispy Chicken Wrap, Grilled Chicken Wrap, Egg Wrap, Mutton Wrap, Fish Wrap, Hummus & Veg Wrap, Caesar Chicken Wrap, BBQ Chicken Wrap, Buffalo Chicken Wrap, Classic Hot Dog, Cheese Hot Dog, Chilli Cheese Dog, Chicken Sausage Dog, Veggie Dog, Corn Dog, Bacon Wrapped Hot Dog, Pretzel Dog. |

#### **Italian & Middle Eastern**

| Dietary Type | Sub-Category | Item Seed List |
| :---- | :---- | :---- |
| **Mixed** | Pizzas (Gourmet) | Margherita Pizza, Marinara Pizza, Quattro Formaggi, Neapolitan Pizza, Caprese Pizza, Funghi Pizza, Ortolana Pizza, Pesto Pizza, Spinach & Feta Pizza, Truffle Pizza, Paneer Tikka Pizza, Chicken Tikka Pizza, Pepperoni Pizza, Prosciutto & Arugula Pizza, BBQ Chicken Pizza, Spicy Salami Pizza, Meat Lovers Pizza, Hawaiian Pizza (Italian Style), Seafood Pizza, Calzone Veg, Calzone Non-Veg, Stromboli Veg, Stromboli Non-Veg, Garlic Bread, Cheese Garlic Bread. |
| **Mixed** | Pastas & Risottos | Penne Arrabbiata, Penne Alfredo, Spaghetti Aglio e Olio, Spaghetti Bolognese, Fettuccine Alfredo, Macaroni & Cheese, Lasagna Al Forno, Veg Lasagna, Ravioli Spinaci, Ravioli Pollo, Tortellini, Gnocchi Pesto, Gnocchi Tomato, Penne Vodka Sauce, Linguine Seafood, Pasta Primavera, Mushroom Risotto, Saffron Risotto, Chicken Risotto, Seafood Risotto, Tomato Risotto, Baked Macaroni, Cannelloni Veg, Cannelloni Meat, Pesto Pasta. |
| **Mixed** | Italian Starters & Mains | Bruschetta Tomato, Bruschetta Mushroom, Caprese Salad, Panzanella Salad, Caesar Salad, Minestrone Soup, Tomato Basil Soup, Arancini (Risotto Balls), Focaccia Bread, Ciabatta Bread, Burrata Plate, Chicken Parmesan, Eggplant Parmesan, Chicken Piccata, Chicken Marsala, Meatballs in Marinara, Polenta, Tiramisu, Panna Cotta, Cannoli. |
| **Mixed** | Middle Eastern | Falafel Wrap, Chicken Shawarma, Mutton Shawarma, Beef Shawarma, Paneer Shawarma, Hummus with Pita, Spicy Hummus, Beetroot Hummus, Baba Ganoush, Moutabal, Labneh, Tzatziki, Fattoush Salad, Tabbouleh, Greek Salad, Veg Mezze Platter, Non-Veg Mezze Platter, Shish Taouk, Chicken Kabsa, Mutton Kabsa, Mandi Rice, Zataar Bread, Cheese Fatayer, Spinach Fatayer, Meat Fatayer, Kibbeh, Dolma, Batata Harra, Baklava, Kunafa. |

#### **Desserts & Beverages**

| Dietary Type | Sub-Category | Item Seed List |
| :---- | :---- | :---- |
| **Vegetarian** | Indian Desserts | Gulab Jamun, Rasmalai, Rasgulla, Jalebi, Rabri, Gajar Ka Halwa, Moong Dal Halwa, Sooji Halwa, Badam Halwa, Kheer, Phirni, Shahi Tukda, Malpua, Mysore Pak, Kaju Katli, Peda, Ladoo (Motichoor), Besan Ladoo, Barfi, Milk Cake, Kalakand, Ghevar, Sandesh, Cham Cham, Kulfi (Malai), Kulfi (Pista), Kulfi (Mango), Falooda, Rajbhog, Basundi. |
| **Mixed** | Bakery & Western | Chocolate Brownie, Walnut Brownie, Sizzling Brownie with Ice Cream, New York Cheesecake, Blueberry Cheesecake, Biscoff Cheesecake, Red Velvet Cake, Black Forest Cake, Chocolate Truffle Cake, Pineapple Cake, Fruit Cake, Lava Cake, Vanilla Ice Cream, Chocolate Ice Cream, Strawberry Ice Cream, Butterscotch Ice Cream, Mint Choc Chip Ice Cream, Donut (Chocolate), Donut (Glazed), Croissant, Chocolate Croissant, Apple Pie, Mud Pie, Tiramisu, Macarons. |
| **Vegetarian** | Hot Beverages | Masala Chai, Ginger Tea, Cardamom Tea, Lemon Tea, Green Tea, Chamomile Tea, Earl Grey Tea, Filter Coffee, Espresso, Americano, Cappuccino, Cafe Latte, Flat White, Mocha, Macchiato, Hot Chocolate, Turmeric Milk, Badam Milk, Hot Malt, Irish Coffee (Non-Alcoholic). |
| **Vegetarian** | Cold Beverages | Cold Coffee, Frappe, Iced Latte, Iced Americano, Mango Lassi, Sweet Lassi, Salted Lassi, Masala Chaas (Buttermilk), Fresh Lime Soda (Sweet), Fresh Lime Soda (Salt), Virgin Mojito, Mint Margarita, Blue Lagoon, Green Apple Soda, Peach Iced Tea, Lemon Iced Tea, Vanilla Milkshake, Chocolate Milkshake, Strawberry Milkshake, Oreo Shake, KitKat Shake, Mango Shake, Banana Shake, Thandai, Coconut Water. |

## **8\. Geospatial Routing, Logistics, and Telemetry**

Bengaluru's complex urban layout requires precise geolocation mechanics. Address strings are structurally insufficient for routing algorithms; the application must force the restaurant operator to drop a pin on an interactive map.

### **8.1 Database and Map Integration**

These coordinates (latitude and longitude) must be stored natively in the PostgreSQL database using the PostGIS extension18. When a Buyer App queries the network, the Seller App must execute an ultra-fast PostGIS query (e.g., ST\_DWithin) to filter out outlets falling outside the feasible delivery radius (e.g., 5-7 kilometers), ensuring food arrives hot and maintaining high service quality metrics18.  
Historically, platforms relied on Google Maps, but changing pricing structures present severe financial risks. Google Maps charges $2.10 per 1,000 dynamic map loads after exhausting an initial 70,000 free monthly loads18. A highly cost-effective alternative for the Indian market is Ola Maps via the Krutrim cloud. Ola offers an aggressive pricing structure that includes up to 5 million free API calls per month for developers18. The frontend can fetch Ola's 2D vector tiles and render them using open-source packages like flutter\_map within the mobile application, effectively bridging advanced geospatial intelligence with sustainable unit economics18.

### **8.2 Kitchen Hardware and Event Streaming**

When an order reaches the /on\_confirm phase, the payload must be transmitted directly to the physical kitchen without dropping requests during high-concurrency traffic spikes. The backend architecture should utilize an internal message broker (Apache Kafka) combined with the Transactional Outbox Pattern18. This design ensures the database transaction and the Kafka dispatch event are committed atomically18.  
The Kafka consumer routes the order to an Android Kitchen Display System (KDS) mounted in the kitchen via persistent WebSockets utilizing the STOMP protocol18. This design eliminates aggressive client polling. When culinary staff finalize the order and interact with the display, the system immediately fires the /on\_status API (triggering the Packed state) to the ONDC network. Simultaneously, the backend can convert the digital order payload into raw ESC/POS byte commands, streaming them over TCP to local thermal network printers to generate physical Kitchen Order Tickets (KOT) directly on the prep line18.

### **8.3 Logistics Execution and Sliding-Scale Economics**

Delivery Executives (DEs) must undergo the identical Penny Drop IMPS verification used for restaurants to validate their bank accounts, preventing failed wage disbursements18. Unlike food preparation venues, DEs do not require FSSAI licenses or GSTINs as they strictly transport sealed goods and generally fall below taxation thresholds18.  
The mobile application for DEs requires high-frequency background location tracking. The mobile app captures GPS coordinates and broadcasts them via WebSockets to the backend, buffering the telemetry in Redis Geospatial structures18. This architecture enables sub-millisecond proximity queries to assign the most optimal driver the moment a kitchen begins preparing an order.  
Balancing executive payouts with customer conversion rates is paramount. A rigid pricing model where customers bear the entire delivery cost cripples order volumes. The optimal strategy implements a sliding-scale dynamic contribution model. For instance, the platform configures a DE payout structure comprising a ₹15 base rate plus ₹8 per kilometer18. The algorithm mandates the restaurant contribute up to a maximum margin cap (e.g., 15% of the total food cost) towards this delivery fee, with the customer covering the remainder18.

| Food Cost | Delivery Distance | Total Executive Payout | Max Restaurant Contribution (15%) | Final Restaurant Share | Final Customer Share | Total Customer Cost (Incl. ₹5 Platform Fee) |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| ₹100 | 2 km | ₹31 | ₹15 | ₹15 | ₹16 | **₹21** |
| ₹100 | 10 km | ₹95 | ₹15 | ₹15 | ₹80 | **₹85** |
| ₹300 | 5 km | ₹55 | ₹45 | ₹45 | ₹10 | **₹15** |
| ₹500 | 10 km | ₹95 | ₹75 | ₹75 | ₹20 | **₹25** |
| ₹800 | 5 km | ₹55 | ₹120 | ₹55 | ₹0 | **₹5** (Platform fee only) |

As illustrated by the data modeling, high-value carts (exceeding ₹800) easily absorb the entire delivery payout within the restaurant's 15% margin limit, driving the customer's variable delivery fee entirely to zero. This algorithm inherently incentivizes larger family orders, driving up the Average Order Value (AOV) while ensuring executives are compensated fairly for distance18.

## **9\. Financial Orchestration and the Reconciliation and Settlement Framework (RSF 2.0)**

In the decentralized ONDC architecture, handling funds necessitates extreme precision to manage split payouts between the Buyer App (which acts as the collector of funds), the Logistics provider, and the Seller App. ONDC recently evolved from manual reconciliation to the Reconciliation & Settlement Framework (RSF 2.0) to standardize this complex multi-party clearing process12.

### **9.1 The NOCS Settlement Agency**

To enforce this framework, the National Payments Corporation of India (NPCI) Bharat BillPay Ltd (NBBL) launched the NOCS platform, functioning as a centralized Settlement Agency (SA) for ONDC transactions20. Instead of relying on manual wire transfers or disjointed ledgers, the RSF 2.0 framework introduces specialized API specifications: /settle, /on\_settle, /recon, /on\_recon, /receiver\_recon, and /on\_receiver\_recon12.  
The standard RSF 2.0 flow orchestrates settlements through strict counter-party validation21:

> 1. **Collection:** The Buyer App collects the total payment from the consumer, effectively becoming the Collector entity21.  
> 2. **Instruction:** The Settlement Agency (SA) performs an order-level match against the respective orders using settlement details received asynchronously from both the Buyer App and the Seller App (Receiver)21.  
> 3. **Execution:** Upon successful reconciliation, the SA passes a debit instruction to the Buyer App's nodal bank account and a corresponding credit instruction to the Seller App's bank account21.  
> 4. **Last-Mile Settlement:** If the Seller App operates as a Marketplace Seller Node (MSN)—meaning the app represents multiple distinct legal entities—the SA can issue further instructions to split the funds and directly credit the individual restaurant's bank account, functioning as the ultimate Seller on Record (SoR)21.

### **9.2 Payment Gateway Integrations and Direct UPI**

For the Buyer App interface collecting the initial funds, platforms traditionally utilize aggregators such as Razorpay or Cashfree. These enterprise services operate on a percentage-based model, charging approximately 1.6% to 2.15% plus an 18% GST on the platform fee per transaction18.  
To achieve true 0% commission on UPI transfers (as mandated by the Indian government for standard bank-to-bank routing), the architecture can leverage Flat-Fee UPI infrastructure providers such as VyaparGateway, which charge a fixed monthly SaaS subscription (e.g., ₹300/month) for unlimited throughput18. These systems bypass the aggregator wallet infrastructure entirely, generating raw UPI intent links that trigger mobile applications like Google Pay or PhonePe directly18.  
Because funds settle into the corporate bank account instantly under a T+0 settlement cycle, there is no centralized wallet balance managed by the gateway. The backend must rely entirely on cryptographically secured server-to-server webhooks to confirm payment success. The provider secures every webhook using an HMAC SHA-256 signature18. The application must implement a global raw body caching filter in the HTTP pipeline to preserve the exact raw bytes of the incoming request for hashing validation prior to JSON deserialization, preventing timing attacks and manipulation18. Handling refunds in this zero-commission UPI ecosystem requires the backend to either execute Reverse IMPS transfers via connected enterprise banking APIs or withhold matching deduction totals from the restaurant's subsequent incoming order queue18.

## **10\. Communication, Rate Limiting, and Telecommunications Compliance**

Operating a high-velocity food delivery network requires an aggressive, multi-channel notification strategy to communicate OTPs, order confirmations, and driver telemetry efficiently and legally.

### **10.1 Distributed Rate Limiting**

To prevent notification spam, protect APIs from abuse, and control infrastructure costs, the system must enforce rate limiting. Implementing algorithms such as the Token Bucket (via the Bucket4j library) backed by a Redis datastore enforces global, distributed rate limiting across all operational microservices18.

### **10.2 Telecom Regulations and DLT Registration**

In the Indian jurisdiction, the Telecom Regulatory Authority of India (TRAI) strictly mandates Distributed Ledger Technology (DLT) registration for any commercial entity transmitting SMS messages. The application must formally register as a Principal Entity with a recognized telecom operator (e.g., Reliance Jio, Bharti Airtel, or Vodafone Idea)18. This process involves a one-time registration fee of approximately ₹5,900 plus GST, which registers the six-letter Sender ID and all SMS message templates on a centralized blockchain ledger to eliminate unsolicited spam18.

### **10.3 Gateway Selection (SMS, WhatsApp, and Push)**

For dispatching mission-critical OTPs and time-sensitive delivery updates, the system must route traffic exclusively through high-priority "Transactional" SMS pipes. These specific routes are legally permitted to bypass Do Not Disturb (DND) registries, ensuring sub-5-second delivery18.  
While enterprise platforms can integrate directly with telecom APIs like JioCX or Airtel IQ, technology ecosystems generally prefer aggregators (such as Fast2SMS, MSG91, or SpringEdge). Aggregators cost between ₹0.11 and ₹0.25 per SMS but provide indispensable network redundancy; if one telecom network experiences congestion, the aggregator instantly reroutes the OTP through an alternative network to ensure successful delivery18. Alternatively, Meta's WhatsApp Business API charges approximately ₹0.115 per utility message in India, providing a richer media experience for sharing dynamic delivery tracking URLs18. For all other high-volume alerts, Firebase Cloud Messaging (FCM) serves as the primary, zero-cost backbone for routing real-time driver locations to the customer application via Push Notifications18.

## **11\. Strategic Conclusions**

Integrating a comprehensive food delivery ecosystem into the ONDC protocol requires far more than basic API orchestration; it necessitates a deep fusion of cryptographic security, geospatial awareness, and resilient financial architecture.  
By leveraging the Beckn protocol and strictly adhering to the ONDC:RET11 taxonomies and transaction lifecycles, the platform ensures seamless interoperability across the network. Applying asymmetric cryptography (Ed25519) and strict BLAKE-512 hashing guarantees the legal non-repudiation of every localized commercial contract. Structuring the database hierarchically protects corporate tax liabilities while enabling precise local dispatching algorithms powered by PostGIS and localized mapping providers.  
Furthermore, optimizing the operational economics—such as employing WebP image delivery via zero-egress edge CDNs, instituting sliding-scale delivery fee algorithms, and integrating with NBBL NOCS for RSF 2.0 settlements—transforms the application from a mere technical interface into a financially viable, highly scalable enterprise entity. For engineering teams deploying in Bengaluru, strict adherence to the ONDC log validation utilities and the deployment of asynchronous event-streaming for hardware kitchen display systems will ensure that the platform not only passes protocol compliance but also dominates in one of the most demanding, high-velocity food delivery markets globally.

#### **Works cited**

> 1. ONDC-Official/ONDC-RET-Specifications \- GitHub, [https://github.com/ONDC-Official/ONDC-RET-Specifications](https://github.com/ONDC-Official/ONDC-RET-Specifications)  
> 2. What is ONDC? Making E-Commerce Easy for Startups \[2026\] \- Treelife, [https://treelife.in/reports/open-network-for-digital-commerce-ondc/](https://treelife.in/reports/open-network-for-digital-commerce-ondc/)  
> 3. ONDC \- GitHub, [https://github.com/ONDC-Official](https://github.com/ONDC-Official)  
> 4. Pramaan \- Toolbox \- ONDC, [https://www.ondc.org/pramaan/toolbox.html](https://www.ondc.org/pramaan/toolbox.html)  
> 5. developer-docs/registry/Onboarding of Participants.md at main · ONDC-Official/developer-docs \- GitHub, [https://github.com/ONDC-Official/developer-docs/blob/main/registry/Onboarding%20of%20Participants.md](https://github.com/ONDC-Official/developer-docs/blob/main/registry/Onboarding%20of%20Participants.md)  
> 6. buddy-core \- Cryptographic Api for Clojure \- cljdoc, [https://cljdoc.org/d/buddy/buddy-core/1.10.1](https://cljdoc.org/d/buddy/buddy-core/1.10.1)  
> 7. Ed25519 signing — Cryptography 50.0.0-dev1 documentation, [https://cryptography.io/en/latest/hazmat/primitives/asymmetric/ed25519/](https://cryptography.io/en/latest/hazmat/primitives/asymmetric/ed25519/)  
> 8. developer-docs/registry/signing-verification.md at main \- GitHub, [https://github.com/ONDC-Official/developer-docs/blob/main/registry/signing-verification.md](https://github.com/ONDC-Official/developer-docs/blob/main/registry/signing-verification.md)  
> 9. Cryptography Documentation, [https://cryptography.io/\_/downloads/en/42.0.2/pdf/](https://cryptography.io/_/downloads/en/42.0.2/pdf/)  
> 10. ONDC-Protocol-Specs/protocol-specifications/core/v0/api/core.yaml at master \- GitHub, [https://github.com/ONDC-Official/ONDC-Protocol-Specs/blob/master/protocol-specifications/core/v0/api/core.yaml](https://github.com/ONDC-Official/ONDC-Protocol-Specs/blob/master/protocol-specifications/core/v0/api/core.yaml)  
> 11. ONDC-Official/woocommerce-adaptor \- GitHub, [https://github.com/ONDC-Official/woocommerce-adaptor](https://github.com/ONDC-Official/woocommerce-adaptor)  
> 12. ONDC-Official/log-validation-utility \- GitHub, [https://github.com/ONDC-Official/log-validation-utility](https://github.com/ONDC-Official/log-validation-utility)  
> 13. chinmaybhatk/Frappe-ONDC-Sellerapp: Frappe v15 app ... \- GitHub, [https://github.com/chinmaybhatk/Frappe-ONDC-Sellerapp](https://github.com/chinmaybhatk/Frappe-ONDC-Sellerapp)  
> 14. ONDC-Official/ondc-sdk \- GitHub, [https://github.com/ONDC-Official/ondc-sdk](https://github.com/ONDC-Official/ondc-sdk)  
> 15. GitHub \- ONDC-Official/v1.2.0-logs: Retail and Logistics Logs for 1.2.0, [https://github.com/ONDC-Official/v1.2.0-logs](https://github.com/ONDC-Official/v1.2.0-logs)  
> 16. A: Developer Guide \- ONDC Resources, [https://resources.ondc.org/tech-resources](https://resources.ondc.org/tech-resources)  
> 17. @ondc/automation-mock-runner \- npm, [https://www.npmjs.com/package/%40ondc%2Fautomation-mock-runner](https://www.npmjs.com/package/%40ondc%2Fautomation-mock-runner)  
> 18. Food Delivery App, uploaded:Food Delivery App  
> 19. How to List Your Restaurant on ONDC in 2025 \- CHUK India, [https://chuk.in/how-to-list-your-restaurant-on-ondc/](https://chuk.in/how-to-list-your-restaurant-on-ondc/)  
> 20. Best 1000 Current Affairs Questions for IBPS RRB Mains 2023 \- Guidely, [https://cdn.guidely.in/pdf/169332228811.pdf](https://cdn.guidely.in/pdf/169332228811.pdf)  
> 21. RSF 2.0 \- AWS, [https://ondc-static-website-media.s3.ap-south-1.amazonaws.com/res/daea2fs3n/image/upload/ondc-website/files/esf\_2\_0\_explainer\_and\_brd\_02\_08\_24.pdf](https://ondc-static-website-media.s3.ap-south-1.amazonaws.com/res/daea2fs3n/image/upload/ondc-website/files/esf_2_0_explainer_and_brd_02_08_24.pdf)  
> 22. Current Affairs Overview and Key Figures | PDF | Government Of India \- Scribd, [https://www.scribd.com/document/674615295/Last-One-Year-Current-Affairs-eBook-July-2023-Edition-English-Medium](https://www.scribd.com/document/674615295/Last-One-Year-Current-Affairs-eBook-July-2023-Edition-English-Medium)