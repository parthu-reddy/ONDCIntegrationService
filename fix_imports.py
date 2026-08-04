import glob, os

files = glob.glob("/Users/parthureddy/Documents/Food Delivery.nosync/ONDCIntegrationService/src/main/java/com/fooddelivery/ondc/beckn/bpp/Bpp*.java")
for f in files:
    with open(f, 'r') as file:
        content = file.read()
    
    if "import org.springframework.transaction.annotation.Transactional;" in content:
        # Remove the misplaced import
        content = content.replace("import org.springframework.transaction.annotation.Transactional;\n\n", "")
        
        # Add it after the package declaration
        lines = content.split('\n')
        new_lines = []
        for line in lines:
            new_lines.append(line)
            if line.startswith("package "):
                new_lines.append("\nimport org.springframework.transaction.annotation.Transactional;")
        
        with open(f, 'w') as file:
            file.write('\n'.join(new_lines))
