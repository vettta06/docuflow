## Требования

- Python 3.10 или выше.
- Внешние библиотеки не требуются.

## Структура

- algorithm_completeness.py — проверка комплектности документов.
- algorithm_accountant.py — выбор бухгалтера с минимальной нагрузкой,
  определение типа документа и извлечение реквизитов из текста.
- test_data.json — тестовые данные для обоих файлов.

## Запуск

Проверка комплектности документов:

    python algorithm_completeness.py

Выбор бухгалтера и демонстрация обработки текста документа:

    python algorithm_accountant.py

## Пример ожидаемого результата

Для algorithm_completeness.py:

    Отсутствующие документы: ['акт']

Для algorithm_accountant.py:

    Accountant with the smallest workload: Иван Петров
