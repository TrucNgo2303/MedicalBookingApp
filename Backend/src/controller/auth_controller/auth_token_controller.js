const jwt = require("jsonwebtoken");
require("dotenv").config();

const check_token = async (req, res) => {
    const token = req.headers["authorization"]?.split(" ")[1];

    if (!token) {
        return res.status(401).json({ valid: false, message: "Không có token" });
    }

    jwt.verify(token, process.env.JWT_SECRET, (err, decoded) => {
        if (err) {
            return res.status(401).json({ valid: false, message: "Token hết hạn hoặc không hợp lệ" });
        }

        res.json({ valid: true, message: "Token hợp lệ", user: decoded });
    });
}

module.exports = {
    check_token
}