/**
 * Implement Gatsby's Node APIs in this file.
 *
 * See: https://www.gatsbyjs.org/docs/node-apis/
 */

const fs = require("fs");
const path = require("path");
const promisify = require("util").promisify;

const writeFile = promisify(fs.writeFile);

// Note: onPreBuild Not called during `gatsby develop`;
// only createPages has graphql and is run in both dev and build
exports.createPages = ({graphql}) => {
    const queryDir = "/tmp/cljs-queries";
    const dataPs = Promise.all(fs.readdirSync(queryDir)
        .filter(f => f.endsWith(".query"))
        .map(f => {
            const queryPath = path.join(queryDir, f);
            const query = fs.readFileSync(queryPath)
                .toString('utf8');
            return graphql(query).then(({errors, data}) => {
                if (errors) {
                  throw new Error(errors.join(`, `));
                }
                return writeFile(
                    queryPath.replace(/\.query$/, '.json'),
                    JSON.stringify(data));
            });
          })
      );
  return dataPs;
}

exports.onCreateWebpackConfig = ({
  stage,
  rules,
  loaders,
  plugins,
  actions,
}) => {
    actions.setWebpackConfig({
      resolve: {
        // Make it easier to resolve cljs-generated files withotu ../..:
        modules: [
            path.resolve(__dirname, "target"),
            "node_modules"]
      }});

// PROBLEM: The replacement triggers re-compilation => loop
//if (stage === "develop" || stage === "build-javascript")
  actions.setWebpackConfig({
    module: {
        rules: [{
            test: /.*\/cljs\/.*\.js$/,
            //test: /.*\.js$/,
            use: path.resolve('./query-replacement-loader.js')
        }]
    },
    // plugins: [],
  });
}
