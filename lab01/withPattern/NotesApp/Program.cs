using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace NotesApp
{
    internal static class Program
    {
        private static Mutex mutex;

        [STAThread]
        static void Main()
        {
            bool createdNew;

            mutex = new Mutex(true, "NotesAppSingletonMutex", out createdNew);

            if (!createdNew)
            {
                MessageBox.Show("Приложение уже запущено!");
                return;
            }

            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new Form1());
        }
    }
}