# importing all the required modules
import PyPDF2
import os
import re
import json
import argparse

def get_pages(pdf_file_path):
    # creating a pdf reader object
    reader = PyPDF2.PdfReader(pdf_file_path)

    # get the number of pages in pdf file
    num_of_pages = len(reader.pages)
    current_page_index = 0
    page_text_list = []
    text = ""
    while current_page_index < num_of_pages and ("*Book Cost" not in text):
        text = reader.pages[current_page_index].extract_text()
        page_text_list.append(text)
        current_page_index += 1
    text = ''.join(page_text_list)
    #print(text)

    return text

# get account information
def get_account_info(text_lines):
    for index, line_str in enumerate(text_lines):
        if "Account No." in line_str:
            return text_lines[index+1].strip().split(' ')

    raise Exception("Invalid Data Format.\nUnable to fetch Investment Account details!")
    
# get portfolio share symbol and quantity
def get_holdings(text_lines):
    canadian_equities_occurrence = 0
    line_index = 0
    while canadian_equities_occurrence != 2:
        if "Canadian Equities" in text_lines[line_index]:
            canadian_equities_occurrence += 1
        line_index += 1

    can_holdings_list = []
    while "US Equities" not in text_lines[line_index]:
        if " $" in text_lines[line_index]:
            can_holdings_list.append(text_lines[line_index].strip().split(' '))
        line_index += 1
    #print(can_holdings_list)

    us_holdings_list = []
    line_index += 1
    while "*Book Cost" not in text_lines[line_index]:
        if " $" in text_lines[line_index]:
            us_holdings_list.append(text_lines[line_index].strip().split(' '))
        line_index += 1       
    us_holdings_list.pop() 
    #print(us_holdings_list)

    return can_holdings_list, us_holdings_list

def get_can_symbol_quantity(can_holdings_list):
    assets = []
    if not can_holdings_list:
        return assets
    
    #can_holdings_list.append("Colonial Coal International Corp CAD 89.0000 89.0000 $38.03 CAD $342.27 $331.85".split(" "))
    #can_holdings_list.append("Eastfield Resources Ltd. ETF 56.0000 56.0000 $38.03 CAD $342.27 $331.85".split(" "))

    for holding in can_holdings_list:
        if holding[0]+holding[1] == "ColonialCoal":
            num_of_units = holding[holding.index("CAD")+2]
            assets.append({"asset": "CAD.TO", "number_of_units": num_of_units, "value_per_unit": num_of_units, "country": "CA", "asset_class": "EQUITY"})

        elif holding[0]+holding[1] == "EastfieldResources":
            num_of_units = holding[holding.index("ETF")+2]
            assets.append({"asset": "ETF.TO", "number_of_units": num_of_units, "value_per_unit": num_of_units, "country": "CA", "asset_class": "EQUITY"})
            
        else:
            for index, str in enumerate(holding):
                if is_valid_symbol(str) and is_float(num_of_units := holding[index+2]):
                    assets.append({"asset": str + ".TO", "number_of_units": num_of_units, "value_per_unit": num_of_units, "country": "CA", "asset_class": "EQUITY"})

    #print(assets)
    return assets

def get_us_symbol_quantity(us_holdings_list):
    assets = []
    if not us_holdings_list:
        return assets
    
    symbol_quantity = {}
    for holding in us_holdings_list:
        for index, str in enumerate(holding):
            num_of_units = holding[index+2]
            if is_valid_symbol(str) and is_float(num_of_units):
                assets.append({"asset": str, "number_of_units": num_of_units, "value_per_unit": num_of_units, "country": "US", "asset_class": "EQUITY"})

    #print(assets)
    return assets

def is_valid_symbol(str):
    pattern = r"^[A-Z]$|^[A-Z][A-Z.]*[A-Z]$"
    return bool(re.search(pattern, str)) and str != "CAD" and str != "ETF" and str != "A" and str != "B" and str != "REIT"

def is_float(str):
    try:
        float(str)
        return True
    except ValueError:
        return False

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--filepath", "-f",
                        help="""Relative or Absolute path of the Account Monthly Statement
                                PDF file to fetch the account data from.""",
                        type=str)
    args = parser.parse_args()

    pdf_file_path = args.filepath
    text_lines = get_pages(pdf_file_path).splitlines()
    account_info = get_account_info(text_lines)[0:3]
    holdings = get_holdings(text_lines)

    account_data = {}
    account_data["meta_data"] = {"file_name": os.path.basename(pdf_file_path)}
    account_data["result_data"] = {"account_num": account_info[0],
                                   "first_name": account_info[1],
                                   "last_name": account_info[2],
                                   "assets": get_can_symbol_quantity(holdings[0]) + get_us_symbol_quantity(holdings[1])}


    print(json.dumps(account_data))

if __name__=="__main__":
    main()