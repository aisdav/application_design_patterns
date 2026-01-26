class User:
    def __init__(self, name: str, email: str, role: str):
        self.name = name
        self.email = email
        self.role = role

    def __str__(self):
        return f"{self.name} | {self.email} | {self.role}"


class UserManager:
    def __init__(self):
        self.users = []

    def add_user(self, user: User):
        self.users.append(user)

    def remove_user(self, email: str):
        self.users = [u for u in self.users if u.email != email]

    def update_user(self, email: str, name: str = None, role: str = None):
        for user in self.users:
            if user.email == email:
                if name is not None:
                    user.name = name
                if role is not None:
                    user.role = role
                break

    def list_users(self):
        for user in self.users:
            print(user)



if __name__ == "__main__":
    manager = UserManager()

    user1 = User("Alice", "alice@mail.com", "Admin")
    user2 = User("Bob", "bob@mail.com", "User")

    manager.add_user(user1)
    manager.add_user(user2)

    print("После добавления:")
    manager.list_users()

    manager.update_user("bob@mail.com", role="Admin")

    print("\nПосле обновления:")
    manager.list_users()

    manager.remove_user("alice@mail.com")

    print("\nПосле удаления:")
    manager.list_users()
