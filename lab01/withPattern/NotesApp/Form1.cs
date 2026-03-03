using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace NotesApp
{
    public partial class Form1: Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private NoteManager manager = NoteManager.GetInstance();

        private void UpdateList()
        {
            listBoxNotes.Items.Clear();

            foreach (var note in manager.GetAll())
            {
                listBoxNotes.Items.Add(note);
            }
        }

        private void buttonAdd_Click(object sender, EventArgs e)
        {
            string text = textBoxNote.Text.Trim();

            if (string.IsNullOrEmpty(text))
            {
                MessageBox.Show("Введите текст заметки!");
                return;
            }

            manager.Add(text);

            UpdateList();

            textBoxNote.Clear();
        }

        private void buttonDelete_Click(object sender, EventArgs e)
        {
            if (listBoxNotes.SelectedItem == null)
            {
                MessageBox.Show("Выберите заметку для удаления!");
                return;
            }

            string selected = listBoxNotes.SelectedItem.ToString();

            manager.Remove(selected);

            UpdateList();
        }

        private void buttonEdit_Click(object sender, EventArgs e)
        {
            if (listBoxNotes.SelectedItem == null)
            {
                MessageBox.Show("Выберите заметку для редактирования!");
                return;
            }

            string selected = listBoxNotes.SelectedItem.ToString();

            EditForm editForm = new EditForm(selected);
            editForm.ShowDialog();

            UpdateList();
        }

        private void listBoxNotes_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (listBoxNotes.SelectedItem != null)
            {
                textBoxNote.Text = listBoxNotes.SelectedItem.ToString();
            }
        }
    }
}
