import fitz
import pytesseract
from PIL import Image
import io
import structlog


logger = structlog.get_logger()


def extract_text_from_bytes(file_bytes: bytes, file_name: str) -> str:
    try:
        if file_name.lower().endswith(".pdf"):
            doc = fitz.open(stream=file_bytes, filetype="pdf")
            text = "".join(page.get_text() for page in doc)
            if not text.strip():
                logger.info("PDF не имеет текстового слоя, распознование через OCR")
                page = doc.load_page(0)
                pix = page.get_pixmap(dpi=200)
                img = Image.open(io.BytesIO(pix.tobytes("png")))
                text = pytesseract.image_to_string(img, lang="rus+eng")
            doc.close()
        else:
            img = Image.open(io.BytesIO(file_bytes))
            text = pytesseract.image_to_string(img, lang="rus+eng")
        return text.strip()
    except Exception as e:
        logger.error("Ошибка OCR", error=str(e))
        return ""
