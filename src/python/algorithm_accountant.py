import json
from pathlib import Path
from typing import Any


def calculate_accountants_load(
    accountants: list[dict[str, Any]],
    accountant_clients: list[dict[str, int]],
) -> dict[int, int]:
    """Calculate the number of assigned organizations for each accountant."""
    loads = {}
    for accountant in accountants:
        accountant_id = accountant["id"]
        load = 0
        for relation in accountant_clients:
            if relation["accountant_id"] == accountant_id:
                load += 1
        loads[accountant_id] = load
    return loads


def choose_accountant(
    accountants: list[dict[str, Any]],
    accountant_clients: list[dict[str, int]],
) -> dict[str, Any] | None:
    """Select the accountant with the smallest workload."""
    loads = calculate_accountants_load(accountants, accountant_clients)
    min_load = float("inf")
    candidate = None
    for accountant in accountants:
        load = loads[accountant["id"]]
        if load < min_load:
            min_load = load
            candidate = accountant
    return candidate


if __name__ == "__main__":
    data_path = Path(__file__).with_name("test_data.json")

    with data_path.open("r", encoding="utf-8") as file:
        data = json.load(file)

    selected = choose_accountant(
        data["accountants"],
        data["accountant_clients"],
    )

    if selected is not None:
        print("Accountant with the smallest workload:", selected["full_name"])
    else:
        print("No accountants available.")
