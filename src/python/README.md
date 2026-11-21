## Требования

- Python 3.10 или выше.
- Внешние библиотеки не требуются.

## Структура

- `algorithm_completeness.py` - проверка комплектности документов.
- `algorithm_accountant.py` - расчёт нагрузки и выбор бухгалтера с минимальной нагрузкой.
- `algorithm_determine_type.py` - определение типа документа по распознанному OCR-тексту.
- `algorithm_extract_data.py` - извлечение реквизитов документа из распознанного OCR-текста.
- `test_data.json` - тестовые данные для алгоритмов.

## Запуск

Проверка комплектности документов:

```bash
python algorithm_completeness.py
```

Выбор бухгалтера:

```bash
python algorithm_accountant.py
```

Определение типа документа:

```bash
python algorithm_determine_type.py
```

Извлечение реквизитов документа:

```bash
python algorithm_extract_data.py
```

По умолчанию каждый скрипт использует файл test_data.json, расположенный в той же директории.

Для проверки на собственных входных данных необходимо изменить содержимое test_data.json, сохранив структуру JSON и названия полей, используемых соответствующим алгоритмом. После этого скрипты можно запустить повторно.

## Пример ожидаемого результата

Для `algorithm_completeness.py`:

```text
Missing documents: ['акт']
```

Для `algorithm_accountant.py`:

```text
Accountant with the smallest workload: Иван Петров
```

Для `algorithm_determine_type.py`:

```text
Счёт № 15: INVOICE
```

Для `algorithm_extract_data.py`:

```text
document_number: 15
document_date: 20.10.2026
amount: 12500.00
```