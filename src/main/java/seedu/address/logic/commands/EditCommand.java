package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
//import static seedu.address.logic.parser.CliSyntax.PREFIX_MOD_ADD;
//import static seedu.address.logic.parser.CliSyntax.PREFIX_MOD_DELETE;
//import static seedu.address.logic.parser.CliSyntax.PREFIX_MOD_NEW;
//import static seedu.address.logic.parser.CliSyntax.PREFIX_MOD_OLD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SKILL_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SKILL_DELETE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SKILL_NEW;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SKILL_OLD;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.skill.Skill;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person in the view panel\n"
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_SKILL_ADD + "SKILL] "
            + "[" + PREFIX_SKILL_DELETE + "SKILL] "
            + "[" + PREFIX_SKILL_OLD + "SKILL " + PREFIX_SKILL_NEW + "SKILL]...\n"
            + "Example: " + COMMAND_WORD
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "The person was not edited.";
    public static final String MESSAGE_DUPLICATE_PERSON =
            "This person already exists in the address book.";
    public static final String MESSAGE_SKILL_DOES_NOT_EXIST =
            "There is a skill you are trying to delete/update that does not exist";
    public static final String MESSAGE_INCORRECT_OLD_NEW_PREFIX =
            "To update existing skills, the prefixes so/ and sn/ must be present";
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(editPersonDescriptor);

        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        Person personToEdit = model.getProtagonist();
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.setProtagonist(editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, editedPerson));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Set<Skill> updatedSkills = editPersonDescriptor.getSkills().orElse(personToEdit.getSkills());

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedSkills);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return editPersonDescriptor.equals(e.editPersonDescriptor);
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Skill> skills;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * @param person
         */
        public EditPersonDescriptor(Person person) {
            this.name = person.getName();
            this.phone = person.getPhone();
            this.email = person.getEmail();
            this.address = person.getAddress();
            this.skills = person.getSkills();
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code skills} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            addSkills(toCopy.skills);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, skills);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Adds {@code skills} to this object's {@code skills}.
         * A defensive copy of {@code skills} is used internally.
         */
        public void addSkills(Set<Skill> skills) {
            Set<Skill> set = new HashSet<>();
            if (skills != null && this.skills != null) {
                set.addAll(this.skills);
                set.addAll(skills);
                this.skills = set;
            } else {
                this.skills = skills;
            }
        }

        /**
         * Deletes {@code skills} from this object's {@code skills}.
         * A defensive copy of {@code skills} is used internally.
         */
        public void deleteSkills(Set<Skill> skills) {
            if (skills != null && this.skills != null) {
                deleteSkillsHelper(skills);
            }
        }

        private void deleteSkillsHelper(Set<Skill> skills) {
            if (hasSkills(skills)) {
                Set<Skill> result = new HashSet<>();
                for (Skill s : this.skills) {
                    if (!skills.contains(s)) {
                        result.add(s);
                    }
                }
                this.skills = result;
            } else {
                this.skills = null;
            }
        }

        /**
         * Updates {@code skills} from this object's {@code skills}.
         * A defensive copy of {@code skills} is used internally.
         */
        public void updateSkills(Set<Skill> oldSkills, Set<Skill> newSkills) {
            if (oldSkills != null && newSkills != null && this.skills != null) {
                updateSkillsHelper(oldSkills, newSkills);
            }
        }

        private void updateSkillsHelper(Set<Skill> oldSkills, Set<Skill> newSkills) {
            if (hasSkills(oldSkills)) {
                Set<Skill> result = new HashSet<>();
                for (Skill s : this.skills) {
                    if (oldSkills.contains(s)) {
                        Skill newSkill = newSkills.iterator().next();
                        result.add(newSkill);
                        newSkills.remove(newSkill);
                    } else {
                        result.add(s);
                    }
                }
                this.skills = result;
            } else {
                this.skills = null;
            }
        }

        private boolean hasSkills(Set<Skill> oldSkills) {
            for (Skill s : oldSkills) {
                if (!this.skills.contains(s)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns an unmodifiable skill set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code skills} is null.
         */
        public Optional<Set<Skill>> getSkills() {
            return (skills != null) ? Optional.of(Collections.unmodifiableSet(skills)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            // state check
            EditPersonDescriptor e = (EditPersonDescriptor) other;

            return getName().equals(e.getName())
                    && getPhone().equals(e.getPhone())
                    && getEmail().equals(e.getEmail())
                    && getAddress().equals(e.getAddress())
                    && getSkills().equals(e.getSkills());
        }
    }
}
