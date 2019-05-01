import re

with open("./src/modeles/emetteur/LecteurSon.java") as file:
    uml_lines = []
    for line in file:
        if "*" in line:
            continue
        clean_line = line.strip()
        if "class" not in clean_line:
            if clean_line.startswith("public") or clean_line.startswith("private"):
                uml_line = ""
                if clean_line.startswith("public"):
                    uml_line += "+ "
                else:
                    uml_line += "- "

                if "static" in clean_line:
                    uml_line += "{static} "
                if "final" in clean_line:
                    uml_line += "final "

                line_tokens = clean_line.split(" ")
                equal_sign_index = -1
                for (i, elm) in enumerate(line_tokens):
                    if elm == "=":
                        equal_sign_index = i

                if equal_sign_index != -1:
                    # type
                    uml_line += line_tokens[equal_sign_index - 2] + " "
                    # name
                    uml_line += line_tokens[equal_sign_index - 1] + " "
                    # equal
                    uml_line += line_tokens[equal_sign_index] + " "
                    # value
                    try:
                        uml_line += line_tokens[equal_sign_index + 1].replace(";", "") + " "
                    except:
                        pass
                else:
                    if "(" not in clean_line:
                        # no equal sign
                        uml_line += line_tokens[-2] + " "
                        uml_line += line_tokens[-1].replace(";", "") + " "

                seen = False
                for (i, token) in enumerate(line_tokens):
                    if "(" in token:
                        uml_line += line_tokens[i - 1] + " "
                        uml_line += token + " "
                        seen = True
                        if ")" in token:
                            break
                    elif seen:
                        if ")" in token:
                            uml_line += token + " "
                            break
                        else:
                            uml_line += token + " "


                uml_lines.append(uml_line)
        else:
            tokens = clean_line.split()
            uml_lines.insert(0, "class " + tokens[2] + " {")
    uml_lines.append("}")
    print("\n".join(uml_lines))
