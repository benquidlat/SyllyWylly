import pandas as pd
from sklearn.model_selection import train_test_split, GridSearchCV, cross_val_score
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.ensemble import RandomForestClassifier
from sklearn.pipeline import Pipeline
from sklearn.metrics import accuracy_score
import joblib

data = pd.read_csv('dataset.csv')

X = data['Event']
y = data['Importance']

pipeline = Pipeline([
    ('vectorizer', TfidfVectorizer(ngram_range=(1, 2))), ('classifier', RandomForestClassifier())
])

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

param_grid = {
    'classifier__n_estimators': [100, 200],
    'classifier__max_depth': [None, 10, 20],
    'classifier__min_samples_split': [2, 5],
    'classifier__min_samples_leaf': [1, 2],
}

grid_search = GridSearchCV(pipeline, param_grid, cv=5)
grid_search.fit(X_train, y_train)

best_classifier = grid_search.best_estimator_

y_pred = best_classifier.predict(X_test)
accuracy = accuracy_score(y_test, y_pred)
print(f'Accuracy: {accuracy:.2f}')

cv_scores = cross_val_score(best_classifier, X, y, cv=5)
print(f'Cross-validated accuracy: {cv_scores.mean():.2f}')

joblib.dump(best_classifier, 'event_classifier_model.pkl')