[table-23e901b1-c48c-4055-80e1-b3de2bdfad0b.csv](https://github.com/user-attachments/files/26395756/table-23e901b1-c48c-4055-80e1-b3de2bdfad0b.csv)Лабораторная работа: паттерн Адаптер (Adapter)

1. Описание проблемы
В приложении CarAdapter пользователь сравнивает автомобили из разных регионов: японские и американские. Каждая группа машин использует собственную систему измерений:
[Характеристика,Японские авто,Американские авто
Скорость,км/ч (getSpeedKmh()),mph (getSpeedMph())
Объём двигателя,литры (getEngineLiters()),куб.дюймы (getEngineCubicInches())
Расход топлива,л/100км (getFuelLPer100()),MPG (getFuelMPG())
Клиренс,мм (getClearanceMm()),дюймы (getClearanceInches())
Разгон,0–100 км/ч (getZeroToHundred()),0–60 mph (getZeroToSixty())
Вес,кг (getWeightKg()),фунты (getWeightLbs())
Цена,йены (getPriceYEN()),доллары (getPriceUSD())Uploading table-23e901b1-c48c-4055-80e1-b3de2bdfad0b.csv…]()
