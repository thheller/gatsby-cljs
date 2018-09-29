const fs = require("fs");

module.exports = function replaceQueryWithData(sourceCode) {
    return sourceCode.replace(
        /"#GRAPHQL:([^:]*):GRAPHQL#"/g,
        (match, sha) => {
            const dataPath = `/tmp/cljs-queries/${sha}.json`;
            if (!fs.existsSync(dataPath)) return match;

            const data = fs.readFileSync(dataPath)
            return data;
        }
    );
}
