package mok.it.app.mokapp.feature.project_edit

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import mok.it.app.mokapp.R
import mok.it.app.mokapp.feature.project_create.CreateProjectFragment
import mok.it.app.mokapp.firebase.service.ProjectService
import mok.it.app.mokapp.model.Project
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
        )
        val cal: Calendar = Calendar.getInstance()
        cal.time = args.project.deadline
        binding.datePicker.updateDate(
            cal[Calendar.YEAR],
            cal[Calendar.MONTH],
            cal[Calendar.DAY_OF_MONTH]
        )
        badgeValue = args.project.maxBadges
        selectedProjectLeader = args.project.projectLeader
        binding.tvBadgeValue.text = badgeValue.toString()
        binding.textViewTitle.text = getString(R.string.edit_project)
        binding.createButton.text = getString(R.string.edit_text)
    }

    override fun getUsers() {
        viewModel.allUsers.observe(viewLifecycleOwner) { users ->
            if (users != null) {
                this.users = users
                names = Array(users.size) { i -> users[i].name }
                projectLeaderDialog()
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
        val editedProject = Project(
            name = binding.projectName.text.toString(),
            description = binding.projectDescription.text.toString(),
            category = binding.projectTerulet.text.toString(),
            maxBadges = badgeValue,
            deadline = deadline,
            projectLeader = selectedProjectLeader,
        )
        ProjectService.updateProject(args.project.id, editedProject)

        return true
    }
}