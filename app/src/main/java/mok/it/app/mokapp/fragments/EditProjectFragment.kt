package mok.it.app.mokapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.R
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.utility.Utility.TAG
import java.util.Calendar
import java.util.Date

@Suppress("DEPRECATION")
class EditProjectFragment : CreateProjectFragment() {

    private val args: EditProjectFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.projectName.setText(args.project.name)
        binding.projectDescription.setText(args.project.description)
        Log.d(TAG, "category dropdown count: ${binding.projectTerulet.adapter.count}")
        binding.projectTerulet.setText(
            args.project.categoryEnum.toString(),
            false
        ) // 0 nem csinál semmit, 1<= kifagy
        //binding.badgeMcs.setSelection(Category.values().indexOf(args.project.categoryEnum))
        val cal: Calendar = Calendar.getInstance()
        cal.time = args.project.deadline
        binding.datePicker.updateDate(
            cal[Calendar.YEAR],
            cal[Calendar.MONTH],
            cal[Calendar.DAY_OF_MONTH]
        )
        badgeValue = args.project.maxBadges
        binding.tvBadgeValue.text = badgeValue.toString()
        binding.textViewTitle.text = getString(R.string.edit_project)
        binding.createButton.text = getString(R.string.edit_text)

        selectedEditors = args.project.leaders.toMutableList()
    }

    override fun getUsers() {
        viewModel.allUsers.observe(viewLifecycleOwner) { users ->
            if (users != null) {
                this.users = users
                names = Array(users.size) { i -> users[i].name }
                checkedNames = BooleanArray(users.size) { i ->
                    args.project.leaders.contains(users[i].documentId)
                }
                initEditorsDialog()
            }
        }
    }

    override fun onCreateBadgePressed() {
        val shouldCloseDialog = onEditBadge()
        if (shouldCloseDialog) {
            findNavController().navigateUp()
        }
    }

    private fun onEditBadge(): Boolean {
        val success = commitEditedBadgeToDatabase()
        return if (success) {
            true
        } else {
            toast(R.string.error_occurred)
            false
        }
    }

    private fun commitEditedBadgeToDatabase(): Boolean {
        val deadline = Date(
            binding.datePicker.year - 1900,
            binding.datePicker.month,
            binding.datePicker.dayOfMonth
        )
        val editedBadge = hashMapOf(
            "category" to binding.projectTerulet.text.toString(),
            "deadline" to deadline,
            "description" to binding.projectDescription.text.toString(),
            "editors" to selectedEditors,
            "name" to binding.projectName.text.toString(),
            "value" to badgeValue,
            //TODO: update icon if a new one was selected, otherwise leave it untouched!
        )
        ProjectService.updateProject(args.project, editedBadge)

        return true
    }
}