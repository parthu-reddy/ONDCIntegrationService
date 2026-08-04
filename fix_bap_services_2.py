import glob, os

files = glob.glob("/Users/parthureddy/Documents/Food Delivery.nosync/ONDCIntegrationService/src/main/java/com/fooddelivery/ondc/beckn/bap/*.java")

for f in files:
    with open(f, 'r') as file:
        content = file.read()
    
    content = content.replace('contextBuilder.buildBapContext(', 'contextBuilder.buildBapRequestContext(')
    content = content.replace('contextBuilder.buildBapRequestContext("search", "std:080")', 'contextBuilder.buildBapRequestContext("search", null, null)')
    content = content.replace('contextBuilder.buildBapRequestContext("select", "std:080")', 'contextBuilder.buildBapRequestContext("select", null, bppUri)')
    content = content.replace('contextBuilder.buildBapRequestContext("init", "std:080")', 'contextBuilder.buildBapRequestContext("init", null, bppUri)')
    content = content.replace('contextBuilder.buildBapRequestContext("confirm", "std:080")', 'contextBuilder.buildBapRequestContext("confirm", null, bppUri)')
    content = content.replace('contextBuilder.buildBapRequestContext("cancel", "std:080")', 'contextBuilder.buildBapRequestContext("cancel", null, bppUri)')
    
    # Fix the missing ondcProperties in BapSearchService
    if "BapSearchService" in content and "contextBuilder.getProperties()" in content:
        content = content.replace("contextBuilder.getProperties().getGatewayUrl()", "ondcProperties.getGatewayUrl()")

    with open(f, 'w') as file:
        file.write(content)
