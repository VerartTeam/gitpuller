# GitPuller





## Private repository

If you want to use a private repository, you need to set up a token. You can easily generate a token on GitHub [here](https://github.com/settings/tokens).
Make sure to select the `repo` scope.

More information on how to set up a token can be found [on the GitHub help page](https://docs.github.com/fr/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens).

The mod first tries to use the token from the environment, then the config file. If the token is provided through the command, it will be used for the current session and override the other methods.

### Environment token

You can use a (system) environment variable to set a token for all sessions.

#### Unix/Linux/macOS:
```bash
export GITPULLER_TOKEN=<token>
```

#### Windows:
CMD:
```cmd
set GITPULLER_TOKEN=<token>
```
Powershell:
```powershell
$Env:GITPULLER_TOKEN = "<token>"
```

### Config file

You can also set up a token in the config file.

```properties
gitpuller.key=<token>
```

### Temporary token

You can set up in game a token for your current session (will be lost after server restart).

```
/git token <token>
```

