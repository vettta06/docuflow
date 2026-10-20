import re


def parse_invoice_data(text: str) -> dict:
    data = {
        "document_type": "INVOICE",
        "number": None,
        "date": None,
        "inn": None,
        "amount": None,
        "errors": []
    }

    match = re.search(r'[Сс]ч[её]т\s*№\s*(\d+)', text)
    if match:
        data["number"] = match.group(1)
    else:
        data["errors"].append("Не удалось определить номер")

    match = re.search(r'(\d{2})\.(\d{2})\.(\d{4})', text)
    if match:
        data["date"] = f"{match.group(3)}-{match.group(2)}-{match.group(1)}"
    else:
        data["errors"].append("Не удалось определить дату")

    match = re.search(r'ИНН\s*(\d{10,12})', text)
    if match:
        data["inn"] = match.group(1)
    else:
        data["errors"].append("Не удалось определить ИНН")

    match = re.search(r'([\d\s]+[.,]?\d*)\s*(?:руб|итого|Итого)', text, re.IGNORECASE)
    if match:
        raw_sum = match.group(1).replace(' ', '').replace(',', '.')
        try:
            data["amount"] = float(raw_sum)
        except ValueError:
            data["errors"].append("Не удалось преобразовать сумму")
    else:
        data["errors"].append("Не удалось определить сумму")

    return data
