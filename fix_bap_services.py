import glob, os

files = glob.glob("/Users/parthureddy/Documents/Food Delivery.nosync/ONDCIntegrationService/src/main/java/com/fooddelivery/ondc/beckn/bap/*.java")

for f in files:
    with open(f, 'r') as file:
        content = file.read()
    
    # Fix BapSearchService
    if "BapSearchService" in content:
        content = content.replace('request.setMessage(java.util.Map.of(\n            "intent", java.util.Map.of(\n                "item", java.util.Map.of("descriptor", java.util.Map.of("name", searchKey)),\n                "fulfillment", java.util.Map.of("type", "Delivery", "end", java.util.Map.of("location", java.util.Map.of("gps", gps)))\n            )\n        ));', 'com.fooddelivery.ondc.dto.OndcMessage ondcMsg = new com.fooddelivery.ondc.dto.OndcMessage();\n        ondcMsg.setIntent(java.util.Map.of(\n            "item", java.util.Map.of("descriptor", java.util.Map.of("name", searchKey)),\n            "fulfillment", java.util.Map.of("type", "Delivery", "end", java.util.Map.of("location", java.util.Map.of("gps", gps)))\n        ));\n        request.setMessage(ondcMsg);')
    
    # Fix BapConfirmService
    if "BapConfirmService" in content:
        content = content.replace('request.setMessage(java.util.Map.of(\n            "order", java.util.Map.of("payment", paymentDetails)\n        ));', 'com.fooddelivery.ondc.dto.OndcMessage ondcMsg = new com.fooddelivery.ondc.dto.OndcMessage();\n        ondcMsg.setOrder(java.util.Map.of("payment", paymentDetails));\n        request.setMessage(ondcMsg);')
    
    # Fix BapInitService
    if "BapInitService" in content:
        content = content.replace('request.setMessage(java.util.Map.of("order", initDetails));', 'com.fooddelivery.ondc.dto.OndcMessage ondcMsg = new com.fooddelivery.ondc.dto.OndcMessage();\n        ondcMsg.setOrder(initDetails);\n        request.setMessage(ondcMsg);')

    # Fix BapSelectService
    if "BapSelectService" in content:
        content = content.replace('request.setMessage(java.util.Map.of("order", selectDetails));', 'com.fooddelivery.ondc.dto.OndcMessage ondcMsg = new com.fooddelivery.ondc.dto.OndcMessage();\n        ondcMsg.setOrder(selectDetails);\n        request.setMessage(ondcMsg);')

    # Fix BapCancelService
    if "BapCancelService" in content:
        content = content.replace('request.setMessage(java.util.Map.of("order_id", orderId, "cancellation_reason_id", cancellationReasonId));', 'com.fooddelivery.ondc.dto.OndcMessage ondcMsg = new com.fooddelivery.ondc.dto.OndcMessage();\n        ondcMsg.setOrder(java.util.Map.of("id", orderId, "cancellation", java.util.Map.of("reason", java.util.Map.of("id", cancellationReasonId))));\n        request.setMessage(ondcMsg);')

    with open(f, 'w') as file:
        file.write(content)

