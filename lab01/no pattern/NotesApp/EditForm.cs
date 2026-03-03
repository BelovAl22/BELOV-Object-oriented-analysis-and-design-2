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
    public partial class EditForm : Form
    {
        public EditForm()
        {
            InitializeComponent();
        }

        public EditForm(NoteManager manager, string noteText)
        {
            InitializeComponent();
            this.manager = manager;
            originalText = noteText;
            textBoxEdit.Text = noteText;
        }

        private NoteManager manager;
        private string originalText;

        private void buttonSave_Click(object sender, EventArgs e)
        {
            string newText = textBoxEdit.Text.Trim();

            if (string.IsNullOrEmpty(newText))
            {
                MessageBox.Show("Текст не может быть пустым!");
                return;
            }

            manager.Remove(originalText);
            manager.Add(newText);

            this.Close();
        }
    }
}
