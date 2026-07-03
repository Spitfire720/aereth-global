from tools.github_snapshot import fetch_snapshot_file
from tools.snapshot_checks import run_basic_aereth_audit


def print_header(title: str):
    print("\n" + "=" * 70)
    print(title)
    print("=" * 70 + "\n")


def show_help():
    print_header("Aereth Local Support Agent")
    print("This is the free/local version.")
    print("It does not call the OpenAI API.")
    print()
    print("Commands:")
    print("  audit")
    print("    Runs the basic Aereth snapshot audit.")
    print()
    print("  file <path>")
    print("    Fetches and prints a file from the GitHub snapshot.")
    print("    Example:")
    print("    file plugins/FragmentEngine/config.yml")
    print()
    print("  manifest")
    print("    Prints manifest.json.")
    print()
    print("  plugins")
    print("    Prints plugin-list.txt.")
    print()
    print("  tree")
    print("    Prints the first part of file-tree.txt.")
    print()
    print("  help")
    print("    Shows this help.")
    print()
    print("  exit")
    print("    Closes the script.")


def handle_command(command: str):
    command = command.strip()

    if not command:
        return

    lowered = command.lower()

    if lowered == "help":
        show_help()
        return

    if lowered == "audit":
        print_header("Basic Aereth Audit")
        print(run_basic_aereth_audit())
        return

    if lowered == "manifest":
        print_header("manifest.json")
        print(fetch_snapshot_file("manifest.json"))
        return

    if lowered == "plugins":
        print_header("plugin-list.txt")
        print(fetch_snapshot_file("plugin-list.txt"))
        return

    if lowered == "tree":
        print_header("file-tree.txt")
        tree = fetch_snapshot_file("file-tree.txt")
        lines = tree.splitlines()
        print("\n".join(lines[:200]))
        if len(lines) > 200:
            print(f"\n... truncated. Total lines: {len(lines)}")
        return

    if lowered.startswith("file "):
        path = command[5:].strip()
        if not path:
            print("Missing file path. Example: file plugins/FragmentEngine/config.yml")
            return

        print_header(path)
        print(fetch_snapshot_file(path))
        return

    print("Unknown command.")
    print("Type: help")


def main():
    show_help()

    while True:
        try:
            command = input("\nAereth local agent > ")
        except KeyboardInterrupt:
            print("\nExiting.")
            break

        if command.strip().lower() in {"exit", "quit"}:
            print("Exiting.")
            break

        handle_command(command)


if __name__ == "__main__":
    main()