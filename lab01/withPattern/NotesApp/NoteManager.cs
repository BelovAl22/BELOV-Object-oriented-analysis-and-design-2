using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NotesApp
{
    public class NoteManager
    {
        private static NoteManager instance;

        private List<string> notes = new List<string>();

        // Закрытый конструктор — нельзя создать извне
        private NoteManager()
        {
        }

        // Глобальная точка доступа
        public static NoteManager GetInstance()
        {
            if (instance == null)
            {
                instance = new NoteManager();
            }

            return instance;
        }

        public void Add(string text)
        {
            notes.Add(text);
        }

        public void Remove(string text)
        {
            notes.Remove(text);
        }

        public List<string> GetAll()
        {
            return new List<string>(notes);
        }
    }
}