// CarAdapter.cpp : Определяет точку входа для приложения.
//

#include "framework.h"
#include "CarAdapter.h"
#include <string>
using namespace std;

#define MAX_LOADSTRING 100

// Глобальные переменные:
HINSTANCE hInst;                                // текущий экземпляр
WCHAR szTitle[MAX_LOADSTRING];                  // Текст строки заголовка
WCHAR szWindowClass[MAX_LOADSTRING];            // имя класса главного окна
HWND hText;

// Отправить объявления функций, включенных в этот модуль кода:
ATOM                MyRegisterClass(HINSTANCE hInstance);
BOOL                InitInstance(HINSTANCE, int);
LRESULT CALLBACK    WndProc(HWND, UINT, WPARAM, LPARAM);
INT_PTR CALLBACK    About(HWND, UINT, WPARAM, LPARAM);

int APIENTRY wWinMain(_In_ HINSTANCE hInstance,
                     _In_opt_ HINSTANCE hPrevInstance,
                     _In_ LPWSTR    lpCmdLine,
                     _In_ int       nCmdShow){
    UNREFERENCED_PARAMETER(hPrevInstance);
    UNREFERENCED_PARAMETER(lpCmdLine);

    // TODO: Разместите код здесь.

    // Инициализация глобальных строк
    LoadStringW(hInstance, IDS_APP_TITLE, szTitle, MAX_LOADSTRING);
    LoadStringW(hInstance, IDC_CARADAPTER, szWindowClass, MAX_LOADSTRING);
    MyRegisterClass(hInstance);

    // Выполнить инициализацию приложения:
    if (!InitInstance (hInstance, nCmdShow))
    {
        return FALSE;
    }

    HACCEL hAccelTable = LoadAccelerators(hInstance, MAKEINTRESOURCE(IDC_CARADAPTER));

    MSG msg;

    // Цикл основного сообщения:
    while (GetMessage(&msg, nullptr, 0, 0))
    {
        if (!TranslateAccelerator(msg.hwnd, hAccelTable, &msg))
        {
            TranslateMessage(&msg);
            DispatchMessage(&msg);
        }
    }

    return (int) msg.wParam;
}



ATOM MyRegisterClass(HINSTANCE hInstance)
{
    WNDCLASSEXW wcex;

    wcex.cbSize = sizeof(WNDCLASSEX);

    wcex.style          = CS_HREDRAW | CS_VREDRAW;
    wcex.lpfnWndProc    = WndProc;
    wcex.cbClsExtra     = 0;
    wcex.cbWndExtra     = 0;
    wcex.hInstance      = hInstance;
    wcex.hIcon          = LoadIcon(hInstance, MAKEINTRESOURCE(IDI_CARADAPTER));
    wcex.hCursor        = LoadCursor(nullptr, IDC_ARROW);
    wcex.hbrBackground  = (HBRUSH)(COLOR_WINDOW+1);
    wcex.lpszMenuName   = MAKEINTRESOURCEW(IDC_CARADAPTER);
    wcex.lpszClassName  = szWindowClass;
    wcex.hIconSm        = LoadIcon(wcex.hInstance, MAKEINTRESOURCE(IDI_SMALL));

    return RegisterClassExW(&wcex);
}

BOOL InitInstance(HINSTANCE hInstance, int nCmdShow)
{
   hInst = hInstance; // Сохранить маркер экземпляра в глобальной переменной

   HWND hWnd = CreateWindowW(szWindowClass, szTitle, WS_OVERLAPPEDWINDOW,
      CW_USEDEFAULT, 0, CW_USEDEFAULT, 0, nullptr, nullptr, hInstance, nullptr);

   if (!hWnd)
   {
      return FALSE;
   }

   ShowWindow(hWnd, nCmdShow);
   UpdateWindow(hWnd);

   return TRUE;
}

// =======================
// ADAPTER PATTERN
// =======================
class ICarAdapter {
public:
    virtual wstring getName() = 0;

    virtual double getMaxSpeedKmh() = 0;
    virtual double getEngineVolume() = 0;
    virtual double getFuelConsumption() = 0;
    virtual double getClearance() = 0;
    virtual double getAcceleration() = 0;
    virtual double getPrice() = 0;
    virtual double getWeight() = 0;
    virtual ~ICarAdapter() {}
};

HBITMAP hToyota, hHonda, hNissan;
HBITMAP hFord, hDodge, hChevy;
int selectedCar1 = 0;
int selectedCar2 = 0;
bool selectFirst = true;
#define ID_TOYOTA 1
#define ID_HONDA 2
#define ID_NISSAN 3
#define ID_FORD 4
#define ID_DODGE 5
#define ID_CHEVY 6

// =======================
// FACTORY CLASS
// =======================
class CarFactory {
public:
    static ICarAdapter* createCar(int id);
};

// =======================
// COMPARATOR CLASS  
// =======================
class CarComparator {
public:
    struct ComparisonResult {
        wstring winner;
        int score1;
        int score2;
        wstring details;
    };

    static ComparisonResult compare(ICarAdapter* c1, ICarAdapter* c2);
};


// ---------- Japanese ----------
class JapaneseCar {
    double speed, engine, fuel, clearance, accel, price, weight; // ← добавили поля
public:
    // ← добавили конструктор
    JapaneseCar(double s, double e, double f, double c, double a, double p, double w)
        : speed(s), engine(e), fuel(f), clearance(c), accel(a), price(p), weight(w) {
    }

    double getSpeedKmh() { return speed; }
    double getEngineLiters() { return engine; }
    double getFuelLPer100() { return fuel; }
    double getClearanceMm() { return clearance; }
    double getZeroToHundred() { return accel; }
    double getPriceYEN() { return price; }
    double getWeightKg() { return weight; }
};

class JapaneseAdapter : public ICarAdapter {
    JapaneseCar* car;  // ← оборачиваем!
    wstring name;
public:
    // ← теперь принимаем car* вместо 7 параметров
    JapaneseAdapter(wstring n, JapaneseCar* c) : name(n), car(c) {}
    ~JapaneseAdapter() override { delete car; }  // ← чистим память

    wstring getName() override { return name; }
    // ← делегируем вызовы обёрнутому объекту
    double getMaxSpeedKmh() override { return car->getSpeedKmh(); }
    double getEngineVolume() override { return car->getEngineLiters(); }
    double getFuelConsumption() override { return car->getFuelLPer100(); }
    double getClearance() override { return car->getClearanceMm(); }
    double getAcceleration() override { return car->getZeroToHundred(); }
    double getPrice() override { return car->getPriceYEN(); }
    double getWeight() override { return car->getWeightKg(); }
};

// ---------- American ----------
class AmericanCar {
    double speed, engineCI, mpg, clearanceIn, accel60, price, weightLbs; // ← поля
public:
    // ← конструктор
    AmericanCar(double s, double e, double m, double c, double a, double p, double w)
        : speed(s), engineCI(e), mpg(m), clearanceIn(c), accel60(a), price(p), weightLbs(w) {
    }

    double getSpeedMph() { return speed; }
    double getEngineCubicInches() { return engineCI; }
    double getFuelMPG() { return mpg; }
    double getClearanceInches() { return clearanceIn; }
    double getZeroToSixty() { return accel60; }
    double getPriceUSD() { return price; }
    double getWeightLbs() { return weightLbs; }
};

class AmericanAdapter : public ICarAdapter {
    AmericanCar* car;  // ← оборачиваем!
    wstring name;
public:
    AmericanAdapter(wstring n, AmericanCar* c) : name(n), car(c) {}
    ~AmericanAdapter() override { delete car; }  // ← чистим память

    wstring getName() override { return name; }
    // ← делегируем + конвертируем единицы
    double getMaxSpeedKmh() override { return car->getSpeedMph() * 1.60934; }
    double getEngineVolume() override { return car->getEngineCubicInches() * 0.016387; }
    double getFuelConsumption() override { return 235.2 / car->getFuelMPG(); }
    double getClearance() override { return car->getClearanceInches() * 25.4; }
    double getAcceleration() override { return car->getZeroToSixty() * 1.03; }
    double getPrice() override { return car->getPriceUSD(); }
    double getWeight() override { return car->getWeightLbs() * 0.453592; }
};

CarComparator::ComparisonResult CarComparator::compare(ICarAdapter* c1, ICarAdapter* c2)
{
    ComparisonResult result = {};
    result.score1 = 0;
    result.score2 = 0;

    wstring details = L"=== " + c1->getName() + L" VS " + c2->getName() + L" ===\n\n";

    auto check = [&](bool cond, wstring name)
        {
            if (cond) {
                result.score1++;
                details += name + L": " + c1->getName() + L"\n";
            }
            else {
                result.score2++;
                details += name + L": " + c2->getName() + L"\n";
            }
        };

    check(c1->getMaxSpeedKmh() > c2->getMaxSpeedKmh(), L"Speed");
    check(c1->getEngineVolume() > c2->getEngineVolume(), L"Engine");
    check(c1->getFuelConsumption() < c2->getFuelConsumption(), L"Fuel");
    check(c1->getClearance() > c2->getClearance(), L"Clearance");
    check(c1->getAcceleration() < c2->getAcceleration(), L"Acceleration");
    check(c1->getPrice() < c2->getPrice(), L"Price");
    check(c1->getWeight() < c2->getWeight(), L"Weight");

    result.details = details;
    result.details += L"\nScore: " + to_wstring(result.score1) + L" : " + to_wstring(result.score2);
    result.winner = (result.score1 > result.score2) ? c1->getName() : c2->getName();
    result.details += L"\nWinner: " + result.winner;

    return result;  // ← Возвращаем структуру
}

ICarAdapter* CarFactory::createCar(int id)
{
    switch (id)
    {
    case ID_TOYOTA:
        return new JapaneseAdapter(L"Toyota", new JapaneseCar(210, 3.3, 9, 230, 6.8, 12704520, 2500));
    case ID_HONDA:
        return new JapaneseAdapter(L"Honda", new JapaneseCar(226, 1.5, 6, 150, 8.4, 3970162, 1200));
    case ID_NISSAN:
        return new JapaneseAdapter(L"Nissan", new JapaneseCar(250, 3.0, 11, 126, 4.5, 67000000, 1634));

    case ID_FORD:
        return new AmericanAdapter(L"Ford", new AmericanCar(202, 307, 15, 6, 4, 65000, 3900));
    case ID_DODGE:
        return new AmericanAdapter(L"Dodge", new AmericanCar(118, 345, 15, 12, 6, 60000, 5500));
    case ID_CHEVY:
        return new AmericanAdapter(L"Chevrolet", new AmericanCar(198, 376, 16, 5.3, 4, 70000, 3800));
    }
    return nullptr;
}

//
//  ФУНКЦИЯ: WndProc(HWND, UINT, WPARAM, LPARAM)
//
//  ЦЕЛЬ: Обрабатывает сообщения в главном окне.
//
//  WM_COMMAND  - обработать меню приложения
//  WM_PAINT    - Отрисовка главного окна
//  WM_DESTROY  - отправить сообщение о выходе и вернуться
//
//
LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
    switch (message)
    {
    case WM_CREATE:
    {
        hToyota = (HBITMAP)LoadImage(NULL, L"landcruiser.bmp", IMAGE_BITMAP, 0, 0, LR_LOADFROMFILE);
        hHonda = (HBITMAP)LoadImage(NULL, L"hondacivic.bmp", IMAGE_BITMAP, 0, 0, LR_LOADFROMFILE);
        hNissan = (HBITMAP)LoadImage(NULL, L"nissanz.bmp", IMAGE_BITMAP, 0, 0, LR_LOADFROMFILE);

        hFord = (HBITMAP)LoadImage(NULL, L"formustang.bmp", IMAGE_BITMAP, 0, 0, LR_LOADFROMFILE);
        hDodge = (HBITMAP)LoadImage(NULL, L"dodgeram.bmp", IMAGE_BITMAP, 0, 0, LR_LOADFROMFILE);
        hChevy = (HBITMAP)LoadImage(NULL, L"chevoletcamaro.bmp", IMAGE_BITMAP, 0, 0, LR_LOADFROMFILE);

        if (!hToyota) MessageBox(hWnd, L"Не удалось загрузить landcruiser.bmp", L"Ошибка", MB_ICONERROR);

        CreateWindow(L"EDIT", L"Toyota",
            WS_VISIBLE | WS_CHILD,
            85, 140, 120, 30,
            hWnd, NULL, hInst, NULL);
        CreateWindow(L"EDIT", L"Honda",
            WS_VISIBLE | WS_CHILD,
            235, 140, 120, 30,
            hWnd, NULL, hInst, NULL);
        CreateWindow(L"EDIT", L"Nissan",
            WS_VISIBLE | WS_CHILD,
            385, 140, 120, 30,
            hWnd, NULL, hInst, NULL);
        CreateWindow(L"EDIT", L"Ford",
            WS_VISIBLE | WS_CHILD,
            85, 250, 120, 30,
            hWnd, NULL, hInst, NULL);
        CreateWindow(L"EDIT", L"Dodge",
            WS_VISIBLE | WS_CHILD,
            235, 250, 120, 30,
            hWnd, NULL, hInst, NULL);
        CreateWindow(L"EDIT", L"Chevrolet",
            WS_VISIBLE | WS_CHILD,
            385, 250, 120, 30,
            hWnd, NULL, hInst, NULL);

        HWND btnToyota = CreateWindow(L"BUTTON", NULL,
            WS_VISIBLE | WS_CHILD | BS_BITMAP,
            50, 50, 120, 90,
            hWnd, (HMENU)1, hInst, NULL);
        SendMessage(btnToyota, BM_SETIMAGE, IMAGE_BITMAP, (LPARAM)hToyota);

        HWND btnHonda = CreateWindow(L"BUTTON", NULL,
            WS_VISIBLE | WS_CHILD | BS_BITMAP,
            200, 50, 120, 90,
            hWnd, (HMENU)2, hInst, NULL);
        SendMessage(btnHonda, BM_SETIMAGE, IMAGE_BITMAP, (LPARAM)hHonda);

        HWND btnNissan = CreateWindow(L"BUTTON", NULL,
            WS_VISIBLE | WS_CHILD | BS_BITMAP,
            350, 50, 120, 90,
            hWnd, (HMENU)3, hInst, NULL);
        SendMessage(btnNissan, BM_SETIMAGE, IMAGE_BITMAP, (LPARAM)hNissan);

        // USA
        HWND btnFord = CreateWindow(L"BUTTON", NULL,
            WS_VISIBLE | WS_CHILD | BS_BITMAP,
            50, 160, 120, 90,
            hWnd, (HMENU)4, hInst, NULL);
        SendMessage(btnFord, BM_SETIMAGE, IMAGE_BITMAP, (LPARAM)hFord);

        HWND btnDodge = CreateWindow(L"BUTTON", NULL,
            WS_VISIBLE | WS_CHILD | BS_BITMAP,
            200, 160, 120, 90,
            hWnd, (HMENU)5, hInst, NULL);
        SendMessage(btnDodge, BM_SETIMAGE, IMAGE_BITMAP, (LPARAM)hDodge);

        HWND btnChevy = CreateWindow(L"BUTTON", NULL,
            WS_VISIBLE | WS_CHILD | BS_BITMAP,
            350, 160, 120, 90,
            hWnd, (HMENU)6, hInst, NULL);
        SendMessage(btnChevy, BM_SETIMAGE, IMAGE_BITMAP, (LPARAM)hChevy);

        CreateWindow(L"BUTTON", L"Switch Selection",
            WS_VISIBLE | WS_CHILD,
            120, 300, 150, 30,
            hWnd, (HMENU)50, hInst, NULL);

        // ===== Compare Button =====
        CreateWindow(L"BUTTON", L"Compare",
            WS_VISIBLE | WS_CHILD,
            120, 330, 120, 40,
            hWnd, (HMENU)10, hInst, NULL);

        // ===== Output =====
        hText = CreateWindow(L"STATIC", L"",
            WS_VISIBLE | WS_CHILD,
            50, 380, 500, 300,
            hWnd, NULL, hInst, NULL);

        break; // ❗ ВАЖНО
    }

    case WM_COMMAND:
    {
        int id = LOWORD(wParam);

        // === Вспомогательная функция для получения имени машины ===
        auto getCarName = [](int carId) -> wstring {
            switch (carId) {
            case ID_TOYOTA: return L"Toyota";
            case ID_HONDA:  return L"Honda";
            case ID_NISSAN: return L"Nissan";
            case ID_FORD:   return L"Ford";
            case ID_DODGE:  return L"Dodge";
            case ID_CHEVY:  return L"Chevrolet";
            default: return L"Unknown";
            }
            };

        // === Обработка выбора автомобиля ===
        if (id == ID_TOYOTA || id == ID_HONDA || id == ID_NISSAN ||
            id == ID_FORD || id == ID_DODGE || id == ID_CHEVY)
        {
            wstring carName = getCarName(id);

            if (selectFirst) {
                selectedCar1 = id;
                wstring msg = L"✓ Car 1: " + carName;
                SetWindowTextW(hText, msg.c_str());  // ← .c_str()!
                selectFirst = false;
            }
            else {
                selectedCar2 = id;
                wstring msg = L"✓ Car 2: " + carName;
                SetWindowTextW(hText, msg.c_str());  // ← .c_str()!
                selectFirst = true;
            }
            break;
        }

        // === Кнопка Switch Selection ===
        if (id == 50) {
            selectFirst = !selectFirst;
            SetWindowTextW(hText, selectFirst ? L"Выбираем машину 1" : L"Выбираем машину 2");
            break;
        }

        // === Кнопка Compare ===
        if (id == 10) {
            if (selectedCar1 == 0 || selectedCar2 == 0) {
                SetWindowTextW(hText, L"Выберите обе машины!");
                break;
            }
            ICarAdapter* c1 = CarFactory::createCar(selectedCar1);
            ICarAdapter* c2 = CarFactory::createCar(selectedCar2);
            if (c1 && c2) {
                CarComparator::ComparisonResult result = CarComparator::compare(c1, c2);
                SetWindowTextW(hText, result.details.c_str());
                delete c1;
                delete c2;
            }
            break;
        }
        break;
    }
    case WM_DESTROY:
        DeleteObject(hToyota); DeleteObject(hHonda); DeleteObject(hNissan);
        DeleteObject(hFord); DeleteObject(hDodge); DeleteObject(hChevy);
        PostQuitMessage(0);
        break;

    default:
        return DefWindowProc(hWnd, message, wParam, lParam);
    }

    return 0;
}// Обработчик сообщений для окна "О программе".
INT_PTR CALLBACK About(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam)
{
    UNREFERENCED_PARAMETER(lParam);
    switch (message)
    {
    case WM_INITDIALOG:
        return (INT_PTR)TRUE;

    case WM_COMMAND:
        if (LOWORD(wParam) == IDOK || LOWORD(wParam) == IDCANCEL)
        {
            EndDialog(hDlg, LOWORD(wParam));
            return (INT_PTR)TRUE;
        }
        break;
    }
    return (INT_PTR)FALSE;
}
