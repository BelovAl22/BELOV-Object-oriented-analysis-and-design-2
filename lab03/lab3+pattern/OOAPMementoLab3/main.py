import tkinter as tk
from tkinter import ttk
from dataclasses import dataclass
import copy

# =========================
# MEMENTO
# =========================

@dataclass
class CarState:
    power: float
    weight: float
    drive: str
    drag: float
    gear_time: float


# =========================
# ORIGINATOR
# =========================

class Car:
    def __init__(self):
        self.power = 150
        self.weight = 1200
        self.drive = "FWD"
        self.drag = 0.3
        self.gear_time = 0.3

    def save(self):
        return CarState(
            self.power,
            self.weight,
            self.drive,
            self.drag,
            self.gear_time
        )

    def restore(self, state: CarState):
        self.power = state.power
        self.weight = state.weight
        self.drive = state.drive
        self.drag = state.drag
        self.gear_time = state.gear_time

    def simulate(self):
        base = self.weight / self.power

        drive_factor = {
            "FWD": 1.15,
            "RWD": 1.05,
            "AWD": 0.95
        }[self.drive]

        shift_time = self.gear_time * 3

        t_0_100 = base * drive_factor + shift_time

        vmax = 0.7 * (self.power / self.drag)

        return round(t_0_100, 2), round(vmax, 1)


# =========================
# CARETAKER
# =========================

class HistoryManager:
    def __init__(self):
        self.undo_stack = []
        self.redo_stack = []

    def save(self, state: CarState):
        self.undo_stack.append(copy.deepcopy(state))
        self.redo_stack.clear()

    def undo(self, current_state):
        if not self.undo_stack:
            return None
        self.redo_stack.append(copy.deepcopy(current_state))
        return self.undo_stack.pop()

    def redo(self, current_state):
        if not self.redo_stack:
            return None
        self.undo_stack.append(copy.deepcopy(current_state))
        return self.redo_stack.pop()


# =========================
# GUI
# =========================

class App:
    def __init__(self, root):
        self.root = root
        self.root.title("Car Simulator (Memento Pattern)")

        self.car = Car()
        self.history = HistoryManager()

        self.create_widgets()
        self.update_ui()

    def create_widgets(self):
        frame = tk.Frame(self.root)
        frame.pack(padx=10, pady=10)

        # Power
        tk.Label(frame, text="Power (hp)").grid(row=0, column=0)
        self.power = tk.Scale(frame, from_=50, to=1000, orient=tk.HORIZONTAL)
        self.power.grid(row=0, column=1)

        # Weight
        tk.Label(frame, text="Weight (kg)").grid(row=1, column=0)
        self.weight = tk.Scale(frame, from_=500, to=3000, orient=tk.HORIZONTAL)
        self.weight.grid(row=1, column=1)

        # Drive
        tk.Label(frame, text="Drive").grid(row=2, column=0)
        self.drive = ttk.Combobox(frame, values=["FWD", "RWD", "AWD"])
        self.drive.grid(row=2, column=1)

        # Drag
        tk.Label(frame, text="Drag Coefficient").grid(row=3, column=0)
        self.drag = tk.Scale(frame, from_=0.2, to=0.6, resolution=0.01, orient=tk.HORIZONTAL)
        self.drag.grid(row=3, column=1)

        # Gearbox
        tk.Label(frame, text="Gearbox").grid(row=4, column=0)
        self.gearbox = ttk.Combobox(frame, values=[
            "Manual (0.5s)",
            "Automatic (0.3s)",
            "Robotized (0.1s)",
            "DSG (0.0s)"
        ])
        self.gearbox.grid(row=4, column=1)

        # Buttons
        tk.Button(frame, text="Simulate", command=self.simulate).grid(row=5, column=0, pady=10)
        tk.Button(frame, text="Save State", command=self.save_state).grid(row=5, column=1)

        tk.Button(frame, text="Undo", command=self.undo).grid(row=6, column=0)
        tk.Button(frame, text="Redo", command=self.redo).grid(row=6, column=1)

        # Results
        self.result = tk.Label(frame, text="Results will appear here", font=("Arial", 12))
        self.result.grid(row=7, column=0, columnspan=2, pady=10)

        # History list
        tk.Label(frame, text="History").grid(row=0, column=2)
        self.history_list = tk.Listbox(frame, height=15, width=30)
        self.history_list.grid(row=1, column=2, rowspan=7, padx=10)

    def get_gear_time(self):
        mapping = {
            "Manual (0.5s)": 0.5,
            "Automatic (0.3s)": 0.3,
            "Robotized (0.1s)": 0.1,
            "DSG (0.0s)": 0.0
        }
        return mapping[self.gearbox.get()]

    def update_car_from_ui(self):
        self.car.power = self.power.get()
        self.car.weight = self.weight.get()
        self.car.drive = self.drive.get()
        self.car.drag = self.drag.get()
        self.car.gear_time = self.get_gear_time()

    def update_ui(self):
        self.power.set(self.car.power)
        self.weight.set(self.car.weight)
        self.drive.set(self.car.drive)
        self.drag.set(self.car.drag)

        gearbox_map = {
            0.5: "Manual (0.5s)",
            0.3: "Automatic (0.3s)",
            0.1: "Robotized (0.1s)",
            0.0: "DSG (0.0s)"
        }
        self.gearbox.set(gearbox_map[self.car.gear_time])

    def simulate(self):
        self.update_car_from_ui()
        t, vmax = self.car.simulate()
        self.result.config(text=f"0-100: {t} s\nMax speed: {vmax} km/h")

    def save_state(self):
        self.update_car_from_ui()
        state = self.car.save()
        self.history.save(state)

        self.history_list.insert(tk.END,
            f"{state.power}hp | {state.weight}kg | {state.drive}")

    def undo(self):
        current = self.car.save()
        state = self.history.undo(current)
        if state:
            self.car.restore(state)
            self.update_ui()
            self.simulate()

    def redo(self):
        current = self.car.save()
        state = self.history.redo(current)
        if state:
            self.car.restore(state)
            self.update_ui()
            self.simulate()


# =========================
# RUN
# =========================

if __name__ == "__main__":
    root = tk.Tk()
    app = App(root)
    root.mainloop()