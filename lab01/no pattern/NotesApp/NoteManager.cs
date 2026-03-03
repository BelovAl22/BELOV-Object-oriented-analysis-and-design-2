using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NotesApp
{
    public class NoteManager
    {
        private List<string> notes = new List<string>();

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
