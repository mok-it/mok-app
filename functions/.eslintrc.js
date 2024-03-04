module.exports = {
  root: true,
  env: {
    es6: true,
    node: true,
  },
  extends: [
    "eslint:recommended",
    "google",
  ],
  rules: {
    "quotes": ["error", "double"],
    "linebreak-style": 0,
  },
};

// Ha ezzel a fájllal lenne valami baj, futtasd:
// .\node_modules\.bin\eslint --fix .eslintrc.js
// Zalán
